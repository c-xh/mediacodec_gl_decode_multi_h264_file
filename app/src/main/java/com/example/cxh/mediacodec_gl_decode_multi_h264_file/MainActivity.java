package com.example.cxh.mediacodec_gl_decode_multi_h264_file;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cxh.mediacodec_gl_decode_multi_h264_file.ChangeView.ChangeViewChannel;
import com.example.cxh.mediacodec_gl_decode_multi_h264_file.ChangeView.ChangeViewMode;
import com.example.cxh.mediacodec_gl_decode_multi_h264_file.opengl.MyGLRenderer;
import com.example.cxh.mediacodec_gl_decode_multi_h264_file.opengl.MyGLSurfaceView;
import com.example.cxh.mediacodec_gl_decode_multi_h264_file.opengl.TextureUtil;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MyGLSurfaceView mainvidew;


    private boolean[] playStatus = {false, false, false, false, false, false};
    private DecodePlayer[] mDecodePlayer = new DecodePlayer[6];

    private Integer[] GLSurfaceViewId = new Integer[]{
            R.id.glview00, R.id.glview01, R.id.glview02,
            R.id.glview03, R.id.glview04, R.id.glview05};
    private MyGLSurfaceView[] mMyGLSurfaceView = new MyGLSurfaceView[6];

    private Integer[] mdecButtonId = new Integer[]{
            R.id.play_bt00, R.id.play_bt01, R.id.play_bt02,
            R.id.play_bt03, R.id.play_bt04, R.id.play_bt05};
    private Button[] decButton = new Button[6];

    ChangeViewMode changeViewMode;
    ChangeViewChannel changeViewChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainvidew = (MyGLSurfaceView) findViewById(R.id.surfaceview);

        for (int i = 0; i < 6; i++) {
            mMyGLSurfaceView[i] = (MyGLSurfaceView) findViewById(GLSurfaceViewId[i]);
            decButton[i] = (Button) findViewById(mdecButtonId[i]);
        }

        changeViewMode = (ChangeViewMode) findViewById(R.id.change_view_mode);
        changeViewChannel = new ChangeViewChannel(MainActivity.this, MainActivity.this, changeViewMode.getViewChannelNum());

        changeViewChannel.setOnChangeViewChannelListener(new ChangeViewChannel.changeViewChannelListener() {
            @Override
            public void ChangeViewChannelOnClick(int[] channelBuff) {
                mainvidew.mRenderer.setChannelBuff(channelBuff);
            }
        });
        changeViewMode.setOnChangeViewModeListener(new ChangeViewMode.changeViewModeListener() {
            @Override
            public void changeViewModeOnClick(int ViewMode, int ViewChannelNum) {
                Log.d(TAG, "changeViewModeOnClick: ViewMode" + ViewMode + "ViewChannel" + ViewChannelNum);
                mainvidew.mRenderer.setDrawMode(ViewMode, ViewChannelNum);
                changeViewChannel.setChannelNum(ViewChannelNum);
            }
        });


        mainvidew.mRenderer.setChannelBuff(changeViewChannel.getChannelBuff());
        mainvidew.mRenderer.setDrawMode(changeViewMode.getViewModes(), changeViewMode.getViewChannelNum());

        //OpenGL的画面更新函数
        initopenglview();
        setDecoder();

    }

    void setDecoder() {
        for (int i = 0; i < decButton.length; i++) {
            final int finalI = i;
            decButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDecodePlayer[finalI] == null) {
                        mDecodePlayer[finalI] = new DecodePlayer(MainActivity.this, new Surface(
                                TextureUtil.getSurfaceTexture(mMyGLSurfaceView[finalI].mRenderer.mGLProgram.get(0).getGlViewSurfaceTextureID())), finalI);
                        mDecodePlayer[finalI].setOnDecoderFpsListener(new DecodePlayer.decoderFpsListener() {
                            @Override
                            public void onRefreshFps(float mFps) {
                                Log.d(TAG, "onRefreshFps" + finalI + ": " + mFps);
                            }
                        });
                    }

                    if (playStatus[finalI]) {
                        decButton[finalI].setText(getResources().getString(R.string.start_dec_str));
                        playStatus[finalI] = false;
                        mDecodePlayer[finalI].pauseDecode();
                    } else {
                        decButton[finalI].setText(getResources().getString(R.string.stop_dec_str));
                        playStatus[finalI] = true;
//                        refreshMaxQueuedNum();
                        mDecodePlayer[finalI].startDecodingThread(5);
                    }
                }
            });
        }
    }

    void initopenglview() {
        mainvidew.mRenderer.setMyopenGLonFrameAvailable(new MyGLRenderer.myOpenGLonFrameAvailable() {
            @Override
            public void onFrameAvailable(int i) {
//                Log.d(TAG, "onFrameAvailable: mainvidew.mRenderer:" + i);
                requestRenderGLView(i);
            }
        });

        for (int i = 0; i < 6; i++) {
            mMyGLSurfaceView[i].mRenderer.setMyopenGLonFrameAvailable(new MyGLRenderer.myOpenGLonFrameAvailable() {
                @Override
                public void onFrameAvailable(int i) {
                    requestRenderGLView(i);
                }
            });
        }
    }

    void requestRenderGLView(int i) {
//        Log.d(TAG, "requestRenderGLView:~~~~~~~~~~~~~~~~~~~~~~~~~~ " + i);
        switch (i) {
            case 0:
                mainvidew.requestRender();
                mMyGLSurfaceView[0].requestRender();
//                FullGLView2.requestRender();
                break;
            case 1:
                mainvidew.requestRender();
                mMyGLSurfaceView[1].requestRender();
//                FullGLView2.requestRender();
                break;
            case 2:
                mainvidew.requestRender();
                mMyGLSurfaceView[2].requestRender();
//                FullGLView2.requestRender();
                break;
            case 3:
                mainvidew.requestRender();
                mMyGLSurfaceView[3].requestRender();
//                FullGLView2.requestRender();
                break;
            case 4:
                mainvidew.requestRender();
                mMyGLSurfaceView[4].requestRender();
//                FullGLView2.requestRender();
                break;
            case 5:
                mainvidew.requestRender();
                mMyGLSurfaceView[5].requestRender();
//                FullGLView2.requestRender();
                break;
//            case 6 : //mainvidew.requestRender();
//                RbsFile_GLView.requestRender();
////                FullGLView2.requestRender();
//                break;
            case 7: //mainvidew.requestRender();
//                FullGLView2.requestRender();
//                RbsLive_GLView.requestRender();
                break;

        }
    }

}
