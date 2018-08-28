package com.example.cxh.mediacodec_gl_decode_multi_h264_file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.Thread.sleep;

/**
 * Created by cxh on 2018/1/16
 * E-mail: shon.chen@rock-chips.com
 */

public class DecodePlayer {

    static final String TAG = "DecodePlayer";

    private static final String DECODER_H264 = "video/avc";
    private static final int VIDEO_WIDTH = 1921;
    private static final int VIDEO_HEIGHT = 1089;

    private Context mContext;
    private byte[] fileBuffer = null;
    private boolean isDecode = true;
    private int testCount = 0;
    private int v1080p = R.raw.tennis200;
//    private int v720p = R.raw.video_1280x720p;
//    private int v2160p = R.raw.video_3840x2160p;

    long restart_time_start = 0;
    boolean f_firstDecodeFrame = false;

    private MediaCodec mMediaCodec;

    private int decodeNum = 0;
    private int numBufferQ = 0;
    private int maxBufferQ = 0;
    private Surface surface = null;
    boolean frameLimit = true;

    public float mFps = 0;
    private float mFrames = 0;
    private long mLastTimestamp = 0;

    private long startDequeueInputBufferTime;
    private long endDequeueInputBufferTime;
    private long startGetInputBufferTime;
    private long endQueueInputBufferTime;
    private long startDequeueOutputBufferTime;
    private long endDequeueOutputBufferTime;


    public DecodePlayer(Context context, Surface mSurface, int i) {
        mContext = context;
        decodeNum = i;
        surface = mSurface;
        initDecode(VIDEO_WIDTH, VIDEO_HEIGHT);
        try {
            fileBuffer = getBytes(new DataInputStream(mContext.getResources().openRawResource(v1080p)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DecodePlayer(Context context, Surface mSurface, int i, int rawFileId, int width, int height) {
        mContext = context;
        decodeNum = i;

        surface = mSurface;
        initDecode(width, height);
        try {
            fileBuffer = getBytes(new DataInputStream(mContext.getResources().openRawResource(rawFileId)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Thread mmDecodeThread;

    public void startDecodingThread(int aMaxQueuedNum) {
        isDecode = true;
        numBufferQ = 0;
        maxBufferQ = 0;
        Log.d(TAG + decodeNum, "startDecodingThread--------------------" + decodeNum);
        Thread mDecodeThread = new Thread(new decodeThread(aMaxQueuedNum));
        mDecodeThread.start();
        mmDecodeThread = mDecodeThread;
    }

    public void pauseDecode() {
        isDecode = false;
        try {
            mmDecodeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pauseDecodeOnfly() {
        isDecode = false;
        try {
            mmDecodeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mMediaCodec.stop();
    }

    private void initDecode(int width, int height) {
        try {
            mMediaCodec = MediaCodec.createDecoderByType(DECODER_H264);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaFormat mediaformat = MediaFormat.createVideoFormat(DECODER_H264, width, height);
        mMediaCodec.configure(mediaformat, surface, null, 0);
        mMediaCodec.start();
    }

    public void stopDecode() {
        isDecode = false;
        try {
            mMediaCodec.stop();
            mMediaCodec.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG + decodeNum, "stopDecode--------------------" + decodeNum);
    }


    /**
     * 解码线程
     */
    private class decodeThread implements Runnable {

        private int kMaxBufQueuedNum = 0;

        decodeThread(int aMaxQueuedNum) {
            kMaxBufQueuedNum = aMaxQueuedNum;
        }

        @Override
        public void run() {
            decodeLoop();
        }

        @SuppressLint("WrongConstant")
        private void decodeLoop() {
//            //解码后的数据，包含每一个buffer的元数据信息，例如偏差，在相关解码器中有效的数据大小
//            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            long timeoutUs = 10000;
            long tview_start = System.currentTimeMillis();
            long tview_cur, fix_interval;
            int startIndex = 0;
            int remaining = fileBuffer.length;

            if (fileBuffer.length == 0) {
                return;
            }

            long pts = 0; // presentation time
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            final boolean mCheckLowLatency = true;

            while (isDecode) {
                fix_interval = (decodeNum == 0) ? 16 : 33;
                if (frameLimit&&(numBufferQ < kMaxBufQueuedNum)) {
                    Log.d(TAG, "decodeLoop: 00000000000000000000000000000");
                    tview_cur = System.currentTimeMillis();
                    if ((tview_cur - tview_start) < fix_interval) {
                        try {
                            sleep(fix_interval - (tview_cur - tview_start) - 1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //Log.e(TAG,"JERRY tview timer " + (tview_cur - tview_start) + " " + fix_interval);
                        continue;
                    } else {
                        tview_start = tview_cur;
                    }
                }

                if (startIndex >= remaining) {
                    startIndex = 0;
                    if (mCheckLowLatency) {
                        numBufferQ = 0;
                        Log.w(TAG, "eos and go to the beginning");
                    }
                }
//                if ((mCheckLowLatency == false) || (numBufferQ < kMaxBufQueuedNum)) {
                    int nextFrameStart = KMPMatch(fileBuffer, startIndex + 2, remaining);
                    if (nextFrameStart == -1) {
                        nextFrameStart = remaining;
                    }
                    //开始时间
                    //long startTime = System.currentTimeMillis();
                    int inIndex = mMediaCodec.dequeueInputBuffer(timeoutUs);
                    //Log.d(TAG + decodeNum, "dequeueInputBuffertime: " + (System.currentTimeMillis() -  startTime ));
                    if (inIndex >= 0) {
                        //long startgetInputBufferTime = System.currentTimeMillis();
                        if (mCheckLowLatency) {
                            if ((nextFrameStart - startIndex) == 30) {
                                numBufferQ = 0;
                            }
                        }
                        mMediaCodec.getInputBuffer(inIndex).put(fileBuffer, startIndex, nextFrameStart - startIndex);
                        if (mCheckLowLatency) {
                            if (numBufferQ == 0 || pts == 0)
                                pts = System.currentTimeMillis();
                            else
                                pts += fix_interval;
                            numBufferQ = numBufferQ + 1;
                            if (numBufferQ > maxBufferQ) maxBufferQ = numBufferQ;
                            Log.w(TAG, "Input encoded frame: pts = " + pts + " ms, idx=" + startIndex + ", size=" + (nextFrameStart - startIndex) + ", remaining=" + remaining + ", numQ=" + numBufferQ);
                        }
                        mMediaCodec.queueInputBuffer(inIndex, 0, nextFrameStart - startIndex, pts * 1000, 0);
                        //Log.d(TAG + decodeNum, "queueInputBuffertime: " + (System.currentTimeMillis() -  startgetInputBufferTime ));
                        startIndex = nextFrameStart;
                    } else {
                        continue;
                    }
//                }

                int indexOutputbuffer = -10;

                long startdequeueOutputBufferTime = System.currentTimeMillis();
                while ((indexOutputbuffer = mMediaCodec.dequeueOutputBuffer(info, 0)) >= 0) {
//                        Log.d(TAG + decodeNum, "dequeueOutputBuffer : " + indexOutputbuffer);
                    switch (indexOutputbuffer) {
                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED://信息输出缓冲区改变
                            Log.w(TAG + decodeNum, " buffer changed : " + indexOutputbuffer);
                            break;
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED://信息输出格式改变了
                            Log.w(TAG + decodeNum, " format changed : " + indexOutputbuffer);
                            break;
                        case MediaCodec.INFO_TRY_AGAIN_LATER://超时
                            Log.w(TAG + decodeNum, " try again later : " + indexOutputbuffer);
                            break;
                        default:

                            if (mCheckLowLatency) {
                                numBufferQ = numBufferQ - 1;
                                //Log.w(TAG, "Got decoded frame, pts = " + info.presentationTimeUs/1000 + " ms, numQ=" + numBufferQ);
                            }
                            endDequeueOutputBufferTime = System.currentTimeMillis();
//                            Log.d(TAG + decodeNum, "dequeueOutputBuffer   : " + (endDequeueOutputBufferTime - startDequeueOutputBufferTime) + " ms    indexOutputbuffer: " + indexOutputbuffer);
//                            Log.d(TAG + decodeNum, "ALLLLLLLLLLLLLLLLLL   : " + (endDequeueOutputBufferTime - endDequeueInputBufferTime) + " ms\n ");
                            ++mFrames;
                            long curtime = System.currentTimeMillis();
                            if (f_firstDecodeFrame == false) {
                                f_firstDecodeFrame = true;
                                //Log.d(TAG, "on-the-fly time= " + (curtime - restart_time_start) + " ms");
                                if (mFirstFrameListener != null) {
                                    mFirstFrameListener.onGetFirstFrame();
                                }
                            }

                            if (mFrames > 10) {
                                mFrames = mFrames * 1000.0f / ((curtime - mLastTimestamp));
                                mFps = mFrames;
                                mFrames = 0;
                                mLastTimestamp = curtime;
                                if (queuedBufNumListener != null) {
                                    queuedBufNumListener.onReportQueuedBufNum(numBufferQ, maxBufferQ);
                                }
                                if (latencyListener != null) {
                                    latencyListener.onReportLatency((curtime * 1000 - info.presentationTimeUs) / 1000);
                                }
                                maxBufferQ = 0;
                                if (listener != null) {
                                    listener.onRefreshFps(mFps);
                                }
                            }
                            mMediaCodec.releaseOutputBuffer(indexOutputbuffer, true);
                            break;
                    }
                }
            }
        }
    }

    public void setFrameLimit(boolean mFrameLimit){
        frameLimit = mFrameLimit;
    }
    public boolean getFrameLimit(){
        return frameLimit;
    }

    private byte[] getBytes(InputStream is) throws IOException {

        int len;
        int size = 1024;
        byte[] buf;
        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1)
                bos.write(buf, 0, len);
//            Log.d(TAG, "getBytes: " + bos.size());
            bos.close();
            buf = bos.toByteArray();
        }
        is.close();
        return buf;
    }

    private int KMPMatch(byte[] bytes, int start, int remain) {

        byte[] pattern = new byte[]{0, 0, 0, 1};
        int[] lsp = {0, 1, 2, 0};//= computeLspTable(pattern);

        int j = 0;  // Number of chars matched in pattern
        for (int i = start; i < remain; i++) {
            while (j > 0 && bytes[i] != pattern[j]) {
                // Fall back in the pattern
                j = lsp[j - 1];  // Strictly decreasing
            }
            if (bytes[i] == pattern[j]) {
                // Next char matched, increment position
                j++;
                if (j == pattern.length)
                    return i - (j - 1);
            }
        }

        return -1;  // Not found
    }

    private decoderFpsListener listener;

    public void setOnDecoderFpsListener(decoderFpsListener listener) {
        this.listener = listener;
    }

    public interface decoderFpsListener {
        public void onRefreshFps(float mFps);
    }

    private FirstFrameListener mFirstFrameListener;

    public void setOnFirstFrameListener(FirstFrameListener listener) {
        this.mFirstFrameListener = listener;
    }

    public interface FirstFrameListener {
        public void onGetFirstFrame();
    }

    private decoderQueuedBufNumListener queuedBufNumListener;

    public void setOnDecoderQueuedBufNumListener(decoderQueuedBufNumListener aListener) {
        this.queuedBufNumListener = aListener;
    }

    public interface decoderQueuedBufNumListener {
        public void onReportQueuedBufNum(int cur, int max);
    }

    private frameLatencyListener latencyListener;

    public void setOnframeLatencyListener(frameLatencyListener aListener) {
        this.latencyListener = aListener;
    }

    public interface frameLatencyListener {
        public void onReportLatency(float aLatencyMS);
    }
}
