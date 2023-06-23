/**
 * FileName: EglSurfaceView
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/23 8:56
 * Description:
 */
package com.flyzebra.eglcamera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class EglSurfaceView extends GLSurfaceView {

    public EglSurfaceView(Context context) {
        super(context);
    }

    public void init(Renderer renderer, SurfaceHolder surfaceHolder) {
        setEGLWindowSurfaceFactory(new EGLWindowSurfaceFactory() {
            @Override
            public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object window) {
                return egl.eglCreateWindowSurface(display, config, surfaceHolder, null);
            }

            @Override
            public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
                egl.eglDestroySurface(display, surface);
            }
        });
        setEGLContextClientVersion(2);
        setRenderer(renderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        setPreserveEGLContextOnPause(true);
    }
}
