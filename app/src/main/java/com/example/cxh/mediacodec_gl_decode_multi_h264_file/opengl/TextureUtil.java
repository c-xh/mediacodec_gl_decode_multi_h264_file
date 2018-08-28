package com.example.cxh.mediacodec_gl_decode_multi_h264_file.opengl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by lixun on 16/9/25.
 */
public  class TextureUtil {
    static SurfaceTexture[] mSurfaceTexture = new SurfaceTexture[8];
    static final String TAG = "TextureUtil~~~";

    public synchronized static SurfaceTexture getSurfaceTexture(int i) {
        Log.d(TAG, "getSurfaceTexture: " + i);
        return mSurfaceTexture[i];
    }

    public synchronized static SurfaceTexture getInstance(int id) {
        if (mSurfaceTexture[id] == null) {
            mSurfaceTexture[id] = new SurfaceTexture(createTextureID());
            try {
                mSurfaceTexture[id].detachFromGLContext();
            } catch (Exception e) {
//                MyLog.sendlog("初次detacht失败");
            }
        }
        return mSurfaceTexture[id];
    }

    public synchronized static SurfaceTexture getInstance() {
        return mSurfaceTexture[0];
    }

    public synchronized static void draw(GLProgram mGLProgram, int mSurfaceTextureID, int mTextureID[], boolean canupdata) {
        getInstance(mSurfaceTextureID).attachToGLContext(mTextureID[mSurfaceTextureID]);
//        if (canupdata)
//            getInstance(mSurfaceTextureID).updateTexImage();
        float[] mtx = new float[16];
        getInstance(mSurfaceTextureID).getTransformMatrix(mtx);
        mGLProgram.draw(mtx);
        getInstance(mSurfaceTextureID).detachFromGLContext();
    }

    public synchronized static void updateTexImage(int mSurfaceTextureID, int mTextureID[]) {
        getInstance(mSurfaceTextureID).attachToGLContext(mTextureID[mSurfaceTextureID]);
        getInstance(mSurfaceTextureID).updateTexImage();
        getInstance(mSurfaceTextureID).detachFromGLContext();
    }

    private synchronized static int createTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }
}
