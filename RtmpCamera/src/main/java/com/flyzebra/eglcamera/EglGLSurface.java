/**
 * FileName: GLES30EGL
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2022/7/20 6:05
 * Description:
 */
package com.flyzebra.eglcamera;

import android.content.Context;
import android.graphics.SurfaceTexture;

import com.flyzebra.camera.Config;
import com.flyzebra.utils.FlyLog;

import java.util.concurrent.atomic.AtomicBoolean;

public class EglGLSurface {
    private Context mContext;
    private EglCamera mEglCamera;
    private EglHelper mEGLHelper;
    private final Object mRecvLock = new Object();
    private final AtomicBoolean isStop = new AtomicBoolean(false);
    private Thread workThread;
    private EglFilter mEglFilter;

    public EglGLSurface(Context context, EglCamera eglCamera) {
        mContext = context;
        mEglCamera = eglCamera;
        mEglFilter = new EglFilter(context);
    }

    public void create() {
        mEGLHelper = new EglHelper();
        isStop.set(false);
        workThread = new Thread(() -> {
            mEGLHelper.onCreate(Config.CAM_W, Config.CAM_H);
            mEglFilter.onCreated();
            while (!isStop.get()) {
                synchronized (mRecvLock) {
                    try {
                        mRecvLock.wait();
                    } catch (InterruptedException e) {
                        FlyLog.e(e.toString());
                    }
                    if (isStop.get()) return;
                }
                mEglFilter.onDraw();
                mEglCamera.upRenderData();
            }
            mEglFilter.onDestory();
            mEGLHelper.onDestory();
        }, "EglDisplay");
        workThread.start();
    }

    public void destory() {
        isStop.set(true);
        synchronized (mRecvLock) {
            mRecvLock.notifyAll();
        }
        try {
            workThread.join();
        } catch (InterruptedException e) {
            FlyLog.e(e.toString());
        }
    }

    public void requestRender() {
        synchronized (mRecvLock) {
            mRecvLock.notify();
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return mEglFilter.getSurfaceTexture();
    }
}
