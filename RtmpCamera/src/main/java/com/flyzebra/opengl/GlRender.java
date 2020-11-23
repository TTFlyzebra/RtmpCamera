package com.flyzebra.opengl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.flyzebra.rtmpcamera.R;
import com.flyzebra.utils.FlyLog;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Author: FlyZebra
 * Time: 18-5-14 下午9:00.
 * Discription: This is GlRender
 */
public class GlRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private Context context;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    //顶点坐标
    static float vertexData[] = {   // in counterclockwise order:
            -1f, -1f, 0.0f, // bottom left
            1f, -1f, 0.0f, // bottom right
            -1f, 1f, 0.0f, // top left
            1f, 1f, 0.0f,  // top right
    };
    //纹理坐标
    static float textureData[] = {   // in counterclockwise order:
            0f, 1f, 0.0f, // bottom left
            1f, 1f, 0.0f, // bottom right
            0f, 0f, 0.0f, // top left
            1f, 0f, 0.0f,  // top right
    };

    private int programId_yuv;
    private int avPosition_yuv;
    private int afPosition_yuv;
    private int sampler_y;
    private int sampler_u;
    private int sampler_v;
    private int[] textureid_yuv;
    int w;
    int h;
    Buffer y;
    Buffer u;
    Buffer v;

    private final Object objectLock = new Object();


    public GlRender(Context context) {
        this.context = context;

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);

    }


    public void setSize(int width, int height) {
        w = width;
        h = height;
        y = ByteBuffer.wrap(new byte[w * h]);
        u = ByteBuffer.wrap(new byte[w * h / 4]);
        v = ByteBuffer.wrap(new byte[w * h / 4]);

    }

    public void pushyuvdata(byte[] yy, byte[] uu, byte[] vv) {
        synchronized (objectLock) {

            //YUV422 TO YUV420
            if (yy.length / uu.length == 2) {
                ((ByteBuffer) y).put(yy, 0, yy.length);
                int length = yy.length + uu.length / 2 + vv.length / 2;
                int index = 0;
                for (int i = yy.length; i < length; i += 2) {
                    ((ByteBuffer) u).put(uu[index]);
                    ((ByteBuffer) v).put(vv[index]);
                    index += 2;
                }
            } else {
                ((ByteBuffer) y).put(yy, 0, 1280 * 720);
                //((ByteBuffer) u).put(uu,0,1280*720/4);
                //((ByteBuffer) v).put(vv,0,1280*720/4);
                for (int i = 0; i < w * h / 4; i++) {
                    if (i % 2 == 0) {
                        ((ByteBuffer) u).put(vv[i]);
                        ((ByteBuffer) v).put(uu[i]);
                    } else {
                        ((ByteBuffer) u).put(uu[i]);
                        ((ByteBuffer) v).put(vv[i]);
                    }
                }
            }
            y.flip();
            u.flip();
            v.flip();
        }
    }

    public void pushyuvdata(byte[] yuv, int width, int height) {
        synchronized (objectLock) {
            ((ByteBuffer) y).put(yuv, 0, width * height);
            ((ByteBuffer) u).put(yuv, width * height, width * height / 4);
            ((ByteBuffer) v).put(yuv, width * height + width * height / 4, width * height / 4);
            y.flip();
            u.flip();
            v.flip();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        FlyLog.d("onSurfaceCreated");
        GLES20.glClearColor(0f, 0f, 0f, 1f);

        //创建一个渲染程序
        String vertexShader = GlShaderUtils.readRawTextFile(context, R.raw.vertex_shader);
        String fragmentShader = GlShaderUtils.readRawTextFile(context, R.raw.fragment_yuv);
        programId_yuv = GlShaderUtils.createProgram(vertexShader, fragmentShader);

        //得到着色器中的属性
        avPosition_yuv = GLES20.glGetAttribLocation(programId_yuv, "av_Position");
        afPosition_yuv = GLES20.glGetAttribLocation(programId_yuv, "af_Position");
        sampler_y = GLES20.glGetUniformLocation(programId_yuv, "sampler_y");
        sampler_u = GLES20.glGetUniformLocation(programId_yuv, "sampler_u");
        sampler_v = GLES20.glGetUniformLocation(programId_yuv, "sampler_v");

        //创建纹理
        textureid_yuv = new int[3];
        GLES20.glGenTextures(3, textureid_yuv, 0);
        for (int i = 0; i < 3; i++) {
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid_yuv[i]);
            //设置环绕和过滤方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        if (w > 0 && h > 0 && y != null && u != null && v != null) {
            GLES20.glUseProgram(programId_yuv);
            GLES20.glEnableVertexAttribArray(avPosition_yuv);
            GLES20.glVertexAttribPointer(avPosition_yuv, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);//为顶点属性赋值
            GLES20.glEnableVertexAttribArray(afPosition_yuv);
            GLES20.glVertexAttribPointer(afPosition_yuv, 3, GLES20.GL_FLOAT, false, 12, textureBuffer);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid_yuv[0]);
            synchronized (objectLock) {
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w, h, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, y);//
            }
            GLES20.glUniform1i(sampler_y, 0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid_yuv[1]);
            synchronized (objectLock) {
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w / 2, h / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, u);
            }
            GLES20.glUniform1i(sampler_u, 1);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid_yuv[2]);
            synchronized (objectLock) {
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w / 2, h / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, v);
            }
            GLES20.glUniform1i(sampler_v, 2);
            y.clear();
            u.clear();
            v.clear();
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        FlyLog.d("updateSurface");
    }

}
