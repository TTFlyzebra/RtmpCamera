/**
 * FileName: CameraRender
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/23 8:57
 * Description:
 */
package com.flyzebra.eglcamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class EglRender implements GLSurfaceView.Renderer {
    private Context mContext;
    private EglFilter mEglFilter;
    private SurfaceTexture mSurfaceTexture;
    private int[] mCameraTexture = new int[1];
    private EglCamera mEglCamera;

    public EglRender(Context context, EglCamera eglCamera) {
        mContext = context;
        mEglCamera = eglCamera;
        mSurfaceTexture = new SurfaceTexture(mCameraTexture[0]);
        mEglFilter = new EglFilter(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mEglFilter.onCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mEglFilter.onDestory();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTexture.updateTexImage();
        mEglFilter.setTextureId(mCameraTexture[0]);
        mEglFilter.onDraw();
        mEglCamera.upRenderData();
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }
}
