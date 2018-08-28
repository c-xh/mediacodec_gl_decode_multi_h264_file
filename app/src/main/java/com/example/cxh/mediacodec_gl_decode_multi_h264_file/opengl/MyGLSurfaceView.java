package com.example.cxh.mediacodec_gl_decode_multi_h264_file.opengl;

import android.content.Context;
import android.content.res.TypedArray;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.example.cxh.mediacodec_gl_decode_multi_h264_file.R;


/**
 * Created by cx1 on 2017/6/30.
 */

public class MyGLSurfaceView extends GLSurfaceView {

    public final MyGLRenderer mRenderer;

    int glSurfaceViewNum;
    int glViewSurfaceTextureID;
    public MyGLSurfaceView(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray attributes  = context.obtainStyledAttributes(attrs, R.styleable.MyGLSurfaceView);
        glSurfaceViewNum = attributes.getInt(R.styleable.MyGLSurfaceView_MyGLSurfaceView_num , 1);
        glViewSurfaceTextureID = attributes.getInt(R.styleable.MyGLSurfaceView_MyGLSurfaceView_ID, 6);
        // 创建OpenGL ES 2.0 的上下文
        setEGLContextClientVersion(2);
        mRenderer = new MyGLRenderer(context , glSurfaceViewNum, glViewSurfaceTextureID);
        // 设置Renderer 到 GLSurfaceView
//        Log.d("MyGLSurfaceView", "MyGLSurfaceView: " + glSurfaceViewNum);
        setRenderer(mRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
