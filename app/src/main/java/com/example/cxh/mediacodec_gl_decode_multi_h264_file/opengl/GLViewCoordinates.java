package com.example.cxh.mediacodec_gl_decode_multi_h264_file.opengl;

import android.util.Log;

/**
 * Created by cxh on 2017/8/8.
 */

public class GLViewCoordinates {
    private float x;
    private float y;
    private float width;
    private float height;
    public GLViewCoordinates(float x , float y ,float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public void coordtoSreing(){
        Log.d("GLViewCoordinates", "x,y(" + x + " , " + y+")"+"width,width(" + width + " , " + width+")");
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
