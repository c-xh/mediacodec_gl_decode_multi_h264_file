package com.example.cxh.mediacodec_gl_decode_multi_h264_file.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cx1 on 2017/6/30.
 */

public class MyGLRenderer  implements GLSurfaceView.Renderer {

    private Context context = null;
    static final String TAG = "MyGLRenderer";


    public List<GLProgram> mGLProgram;
    //6种画面分割模式的窗口
    private float[][][] switchMode3D = new float[][][]{{{0, 0, 1920, 1088}},
            {{0, 0, 1920, 1088}, {960, 0, 960, 544}},
            {{0, 272, 960, 544}, {960, 272, 960, 544}},
            {{960, 272, 960, 544}, {0, 0, 960, 544}, {0, 544, 960, 544}},
            {{0, 270, 960, 540}, {960, 0, 960, 540}, {960, 544, 960, 540}},
            {{0, 184, 1280, 720}, {1280, 0, 640, 360}, {1280, 360, 640, 360}, {1280, 720, 640, 360}},
            {{0, 136, 1440, 816}, {1440, 0, 480, 272}, {1440, 272, 480, 272}, {1440, 544, 480, 272}, {1440, 816, 480, 272}},
            {{0, 363, 1280, 725}, {0, 0, 640, 363}, {640, 0, 640, 363}, {1280, 0, 640, 363}, {1280, 363, 640, 363}, {1280, 726, 640, 362}},
            {{0, 180, 640, 360}, {640, 180, 640, 360}, {1280, 180, 640, 360}, {0, 540, 640, 360}, {640, 540, 640, 360}, {1280, 540, 640, 360}}};

    // 屏幕的宽度
    private float mScreenWidth = 0;
    // 屏幕的高度
    private float mScreenHeight = 0;
    //GL窗口的个数
    private int glSurfaceView_num = 1;

    private int glViewSurfaceTextureID = 7;

    public MyGLRenderer(Context context, int glSurfaceView_num, int glViewSurfaceTextureID) {
        this.context = context;
        this.glSurfaceView_num = glSurfaceView_num;
        if (glSurfaceView_num == 1) {
            this.glViewSurfaceTextureID = glViewSurfaceTextureID;
//            Log.d("GLProgram", "this.MyGLSurfaceView_ID: " + this.MyGLSurfaceView_ID + "    MyGLSurfaceView_ID:" + MyGLSurfaceView_ID);
        }
        mGLProgram = new ArrayList<>();
        mGLViewCoordinates = new ArrayList<>();
    }

    //    在 surface 创建时被回调，通常用于进行初始化工作，只会被回调一次；
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // 设置背景的颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        if (glSurfaceView_num > 1) {
            for (int i = 0; i < glSurfaceView_num; i++) {
                mGLProgram.add(new GLProgram(context, mScreenWidth, mScreenHeight, i));
                mGLViewCoordinates.add(new GLViewCoordinates(0, 0, mScreenWidth, mScreenHeight));
            }
        } else {
            mGLProgram.add(new GLProgram(context, mScreenWidth, mScreenHeight, glViewSurfaceTextureID));
            mGLViewCoordinates.add(new GLViewCoordinates(0, 0, mScreenWidth, mScreenHeight));
        }

        for (int i = 0; i < mGLProgram.size(); i++) {
//            Log.d(TAG, "mRenderer.mGLProgram.get(i): " + "mainvidew" + i);
            mGLProgram.get(i).setopenGLonFrameAvailable(new GLProgram.openGLonFrameAvailable() {
                @Override
                public void onFrameAvailable(int i) {
                    if (listener != null)
                        listener.onFrameAvailable(i);
//                    requestRenderGLView(i);
//                    Log.d(TAG, "mRenderer.mGLProgram.get(i): " + "mainvidew" + i);
                }
            });
        }
    }


    //在每次 surface 尺寸变化时被回调，注意，第一次得知 surface 的尺寸时也会回调
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {

        mScreenWidth = width;
        mScreenHeight = height;
        GLES20.glViewport(0, 0, width, height);

        for (int i = 0; i < mGLProgram.size(); i++) {
            GLProgram mGLProgram_t = mGLProgram.get(i);
            mGLProgram_t.setScreenWidth(mScreenWidth);
            mGLProgram_t.setScreenHeight(mScreenHeight);
            mGLProgram.set(i, mGLProgram_t);
            if (switchMode3D[drawMode - 1].length > i) {
                setGLProgramviewmode(switchMode3D[drawMode - 1][i], i);
            } else {
                continue;
            }
        }

    }

    //保存纹理窗口坐标
    private List<GLViewCoordinates> mGLViewCoordinates;

    public void setGLProgramviewmode(float[] mode, int num) {
        GLViewCoordinates mGLViewCoordinates_t = new GLViewCoordinates(
                (mScreenWidth * mode[0]) / 1920.0f, (mScreenHeight * mode[1]) / 1088.0f,
                (mScreenWidth * mode[2]) / 1920.0f, (mScreenHeight * mode[3]) / 1088.0f);
        mGLViewCoordinates.set(num, mGLViewCoordinates_t);
    }


    private int drawMode = 1;
    private int viewChannelNum = 1;
    private int[] channelBuff = new int[]{0, 0, 0, 0, 0, 0, 0, 0};

    public void setDrawMode(int drawMode, int viewChannelNum) {
        this.drawMode = drawMode;
        this.viewChannelNum = viewChannelNum;

//        Log.d("onSurfaceChanged", "width,height: (" + mScreenWidth + " , "+ mScreenWidth + ")");
        for (int i = 0; i < mGLProgram.size(); i++) {
            if (switchMode3D[this.drawMode - 1].length > i) {
                GLProgram mGLProgram_t = mGLProgram.get(i);
                mGLProgram_t.setScreenWidth(this.mScreenWidth);
                mGLProgram_t.setScreenHeight(this.mScreenHeight);
                mGLProgram.set(i, mGLProgram_t);
                setGLProgramviewmode(switchMode3D[this.drawMode - 1][i], i);
            } else {
                break;
            }
        }
    }

    public void setChannelBuff(int[] channelBuff) {
        this.channelBuff = channelBuff;
    }

    public int[] getChannelBuff() {
        return channelBuff;
    }

    public int getDrawMode() {
        return drawMode;
    }

    public int getViewChannelNum() {
        return viewChannelNum;
    }

    public void setOpenGLPlayStatus(boolean openGLPlayStatus) {
        this.openGLPlayStatus = openGLPlayStatus;
    }

    boolean openGLPlayStatus = false;

    @Override
    public void onDrawFrame(GL10 unused) {
        // 重绘背景颜色
        if (openGLPlayStatus) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            return;
        }
        for (int i = 0; i < mGLProgram.size(); i++) {
            TextureUtil.updateTexImage(mGLProgram.get(i).getGlViewSurfaceTextureID(), mGLProgram.get(i).getGLProgram_textures());
        }

        if (mGLProgram.size() == 1) {
            GLProgram mGLProgram_t = mGLProgram.get(0);
            mGLProgram_t.resetMatrix();
            TextureUtil.draw(mGLProgram_t, mGLProgram_t.getGlViewSurfaceTextureID()
                    , mGLProgram_t.getGLProgram_textures()
                    , mGLProgram_t.GetFrameAvailable(mGLProgram_t.getGlViewSurfaceTextureID()));
        } else if (mGLProgram.size() > 1) {
            for (int i = 0; i < viewChannelNum; i++) {
                GLProgram mGLProgram_t = mGLProgram.get(channelBuff[i]);
                mGLProgram_t.setMatrix(
                        mGLViewCoordinates.get(i).getX(), mGLViewCoordinates.get(i).getY(),
                        mGLViewCoordinates.get(i).getWidth(), mGLViewCoordinates.get(i).getHeight());
                TextureUtil.draw(mGLProgram_t, mGLProgram_t.getGlViewSurfaceTextureID(),
                        mGLProgram_t.getGLProgram_textures(),
                        mGLProgram_t.GetFrameAvailable(mGLProgram_t.getGlViewSurfaceTextureID()));
            }
        }
    }


    private boolean onDrawFrameStatus = false;

    public void setonDrawFrameStatus(boolean monDrawFrameStatus) {
        onDrawFrameStatus = monDrawFrameStatus;
    }

    private myOpenGLonFrameAvailable listener;

    public void setMyopenGLonFrameAvailable(myOpenGLonFrameAvailable listener) {
        this.listener = listener;
    }

    public interface myOpenGLonFrameAvailable {
        public void onFrameAvailable(int i);
    }

}