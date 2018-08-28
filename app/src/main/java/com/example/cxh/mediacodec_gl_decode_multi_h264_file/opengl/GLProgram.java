package com.example.cxh.mediacodec_gl_decode_multi_h264_file.opengl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;


import com.example.cxh.mediacodec_gl_decode_multi_h264_file.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by cx1 on 2017/6/30.
 */

public class GLProgram{

    private FloatBuffer vertexBuffer;
    Context context = null;


    private int mMVPMatrixHandle;

    private static float hSize = 1.0f;
    private static float wSize = 16.0f/16.0f;

    private static float squareCoords[] = {
            -hSize, wSize,   // top left
            -hSize, -wSize,   // bottom left
            hSize, -wSize,    // bottom right
            hSize, wSize}; // top right

    private static short drawOrder[] = {0, 1, 2, 0, 2, 3};// 绘制顶点的顺序

    private FloatBuffer textureBuffer;
    private float textureCoords[] = {
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f};
    private static int[] textures = new int[8];

    private int mPositionHandle;

    // 屏幕的宽度
    private float screenWidth;
    // 屏幕的高度
    private float screenHeight;

    public int getGlViewSurfaceTextureID() {
        return glViewSurfaceTextureID;
    }

    private int glViewSurfaceTextureID =7;

    /**
     * GLProgram初始化
     *
     * @param context                   activity的上下文用于一些参数的引用
     * @param screenWidth               当前GLSurfaceView的窗口宽
     * @param screenHeight              当前GLSurfaceView的窗口高
     * @param glViewSurfaceTextureID    当前GLProgram对应的SurfaceTexture编号
     */
    public GLProgram(Context context, float screenWidth, float screenHeight , int glViewSurfaceTextureID){
        this.context = context;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        if(glViewSurfaceTextureID <=7)
            this.glViewSurfaceTextureID = glViewSurfaceTextureID;
//        Log.d("GLProgram", "this.glViewSurfaceTextureID: " + this.glViewSurfaceTextureID + "    glViewSurfaceTextureID" + glViewSurfaceTextureID);
        setupGraphics();
        setupVertexBuffer();
        setupTexture();
    }


    public void setScreenWidth(float screenWidth) {
        this.screenWidth = screenWidth;
    }
    public void setScreenHeight(float screenHeight) {
        this.screenHeight = screenHeight;
    }

    private int shaderProgram;
    int textureParamHandle;
    int textureCoordinateHandle;
    int positionHandle;
    int textureTranformHandle;

    private void setupGraphics() {
        final String vertexShader = RawResourceReader.readTextFileFromRawResource(context,  R.raw.vetext_sharder);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(context, R.raw.fragment_sharder);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
        shaderProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"texture", "vPosition", "vTexCoordinate", "textureTransform"});

        GLES20.glUseProgram(shaderProgram);
        textureParamHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
        textureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "vTexCoordinate");
        positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition");
        textureTranformHandle = GLES20.glGetUniformLocation(shaderProgram, "textureTransform");
    }
    private ShortBuffer drawListBuffer;
    private void setupVertexBuffer() {
        // Draw list buffer
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // Initialize the texture holder
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);
    }
    private void setupTexture() {
        ByteBuffer texturebb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        texturebb.order(ByteOrder.nativeOrder());

        textureBuffer = texturebb.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);

        // Generate the actual texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glGenTextures(8, textures, 0);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[glViewSurfaceTextureID]);

        TextureUtil.getInstance(glViewSurfaceTextureID).setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                synchronized (this) {
                    frameAvailable[glViewSurfaceTextureID] = true;
                    if(listener != null)
                        listener.onFrameAvailable(glViewSurfaceTextureID);
//                    Log.d("vvv", "onFrameAvailable: " + glViewSurfaceTextureID);
                }
            }
        });

    }

    //声明点击监听
    private openGLonFrameAvailable listener;

    //向外提供一个设置监听的方法
    public void setopenGLonFrameAvailable(openGLonFrameAvailable listener){
        this.listener = listener;
    }

    //创建一个监听按钮点击的接口
    public interface openGLonFrameAvailable{
        public void onFrameAvailable(int i);
    }


    public int[] getGLProgram_textures() {
        return textures;
    }

    public float[] mMVP = new float[16];
    public void resetMatrix() {
        mat4f_LoadOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, mMVP);
    }
    public static void mat4f_LoadOrtho(float left, float right, float bottom, float top, float near, float far, float[] mout) {
        float r_l = right - left;
        float t_b = top - bottom;
        float f_n = far - near;
        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        mout[0] = 2.0f / r_l;
        mout[1] = 0.0f;
        mout[2] = 0.0f;
        mout[3] = 0.0f;

        mout[4] = 0.0f;
        mout[5] = 2.0f / t_b;
        mout[6] = 0.0f;
        mout[7] = 0.0f;

        mout[8] = 0.0f;
        mout[9] = 0.0f;
        mout[10] = -2.0f / f_n;
        mout[11] = 0.0f;

        mout[12] = tx;
        mout[13] = ty;
        mout[14] = tz;
        mout[15] = 1.0f;
    }
    public void setMatrix(float x , float y ,float Width, float Height){
        calculateMatrix( x, y, Width, Height, this.screenWidth, this.screenHeight);
    }
    public void calculateMatrix(float x , float y ,float Width, float Height, float screenWidth, float screenHeight ) {
        Matrix.setIdentityM(mMVP, 0);
        float scaleX = Width/screenWidth;
        float scaleY =Height/screenHeight;
        float ratioX = -(screenWidth/Width -  1)+(2 * x)/Width;
        float ratioY = -(screenHeight/Height -  1)+(2 * y)/Height;
        Matrix.scaleM(mMVP, 0, scaleX, scaleY, 0);                  //缩小
        Matrix.translateM(mMVP, 0, ratioX , ratioY , 0f);           //平移
    }

    private boolean[] frameAvailable = new boolean[]{false, false, false, false, false, false, false, false};
//    private float[] videoTextureTransform = new float[16];

    public synchronized boolean GetFrameAvailable(int i){

        if (frameAvailable[i]) {
            frameAvailable[i] = false;
            return true;
        }
        return false;
    }

    public void draw(float[] mtxTextureTransform) {
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[glViewSurfaceTextureID]);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(textureParamHandle, 0);

        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        GLES20.glVertexAttribPointer(textureCoordinateHandle, 4, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glUniformMatrix4fv(textureTranformHandle, 1, false, mtxTextureTransform, 0);

//        // 获得形状的变换矩阵的句柄
        mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVP, 0);


        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle);

    }
}
