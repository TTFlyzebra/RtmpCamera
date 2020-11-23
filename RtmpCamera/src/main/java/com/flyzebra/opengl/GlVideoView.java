package com.flyzebra.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.flyzebra.rtmp.FlvRtmpClient;


/**
 * Author: FlyZebra
 * Time: 18-5-14 下午9:00.
 * Discription: This is GlVideoView
 */
public class GlVideoView extends GLSurfaceView implements SurfaceHolder.Callback {
    private GlRender glRender;

    public GlVideoView(Context context) {
        this(context, null);
    }

    public GlVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);
        glRender = new GlRender(context);
        glRender.setSize(FlvRtmpClient.VIDEO_WIDTH,FlvRtmpClient.VIDEO_HEIGHT);
        setRenderer(glRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        getHolder().addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        super.surfaceCreated(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        super.surfaceChanged(surfaceHolder, i, i1, i2);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        super.surfaceDestroyed(surfaceHolder);
    }

    public void pushyuvdata(byte[] y,byte[] u, byte[] v) {
        glRender.pushyuvdata(y,u,v);
        requestRender();
    }


    public void pushyuvdata(byte[] yuv,int width, int height) {
        glRender.pushyuvdata(yuv,width,height);
        requestRender();
    }

}
