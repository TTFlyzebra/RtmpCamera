/**
 * FileName: EGLHelper
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2022/7/21 8:16
 * Description:
 */
package com.flyzebra.camera.camera;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

public class EglHelper {
    public EGL10 mEgl;
    public EGLDisplay mEglDisplay;
    public EGLConfig mEglConfig;
    public EGLSurface mEglSurface;
    public EGLContext mEglContext;
    public GL10 mGL;

    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

    private int red = 8;
    private int green = 8;
    private int blue = 8;
    private int alpha = 8;
    private int depth = 16;
    private int renderType = 4;
    private EGLContext shareContext = EGL10.EGL_NO_CONTEXT;

    public void onCreate(int width, int height) {
        int[] attributes = new int[]{
                EGL10.EGL_RED_SIZE, red,  //指定RGB中的R大小（bits）
                EGL10.EGL_GREEN_SIZE, green, //指定G大小
                EGL10.EGL_BLUE_SIZE, blue,  //指定B大小
                EGL10.EGL_ALPHA_SIZE, alpha, //指定Alpha大小，以上四项实际上指定了像素格式
                EGL10.EGL_DEPTH_SIZE, depth, //指定深度缓存(Z Buffer)大小
                EGL10.EGL_RENDERABLE_TYPE, renderType, //指定渲染api版本, EGL14.EGL_OPENGL_ES2_BIT
                EGL10.EGL_NONE};  //总是以EGL10.EGL_NONE结尾

        //获取Display
        mEgl = (EGL10) EGLContext.getEGL();
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        int[] version = new int[2];    //主版本号和副版本号
        mEgl.eglInitialize(mEglDisplay, version);
        //选择Config
        int[] configNum = new int[1];
        mEgl.eglChooseConfig(mEglDisplay, attributes, null, 0, configNum);
        if (configNum[0] == 0) {
            return;
        }
        EGLConfig[] c = new EGLConfig[configNum[0]];
        mEgl.eglChooseConfig(mEglDisplay, attributes, c, configNum[0], configNum);
        mEglConfig = c[0];
        //创建Surface
        int[] surAttr = new int[]{EGL10.EGL_WIDTH, width, EGL10.EGL_HEIGHT, height, EGL10.EGL_NONE};
        mEglSurface = mEgl.eglCreatePbufferSurface(mEglDisplay, mEglConfig, surAttr);
        //创建Context
        int[] contextAttr = new int[]{EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};
        mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig, shareContext, contextAttr);
        makeCurrent();
    }

    public void makeCurrent() {
        mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext);
        mGL = (GL10) mEglContext.getGL();
    }

    public void onDestory() {
        mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
        mEgl.eglDestroyContext(mEglDisplay, mEglContext);
        mEgl.eglTerminate(mEglDisplay);
    }
}
