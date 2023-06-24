/**
 * FileName: EglCamera
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/23 8:49
 * Description:可以通过OPENGL获取相机的原始RGB数据
 */
package com.flyzebra.camera.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.opengl.GLES30;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.flyzebra.libyuv.FlyYuv;
import com.flyzebra.utils.FlyLog;
import com.flyzebra.utils.SPUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EglCamera{
    private Context mContext;
    private TextureView mTextureView;
    private int cam_w = 720;
    private int cam_h = 1280;

    private ByteBuffer frameRGBA;
    private byte[] nv12;

    private final String CAMERA_ID_KEY = "CAMERA_ID_KEY";
    public static String cameraID = "1";

    private CameraManager mCameraManager;
    private CaptureRequest.Builder mBuilder;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private HandlerThread mCamThread;
    private Handler mCamBkHandler;
    private EglGLSurface mEglGLSurface;
    private AtomicBoolean is_opened = new AtomicBoolean(false);
    private static final Object mFrameLock = new Object();
    private Thread wFrameThread;

    public EglCamera(Context context, TextureView textureView, int width, int height) {
        mContext = context;
        mTextureView = textureView;
        cam_w = width;
        cam_h = height;
        nv12 = new byte[cam_w * cam_h * 3 / 2];
        frameRGBA = ByteBuffer.wrap(new byte[cam_w * cam_h * 4]);

        mCamThread = new HandlerThread("camera2");
        mCamThread.start();
        mCamBkHandler = new Handler(mCamThread.getLooper());
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        cameraID = (String) SPUtil.get(mContext, CAMERA_ID_KEY, "1");
        mEglGLSurface = new EglGLSurface(mContext, this);
    }

    public void openCamera() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            return;
        try {
            mCameraManager.openCamera(cameraID, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    try {
                        mCameraDevice = camera;
                        CameraCharacteristics c = mCameraManager.getCameraCharacteristics(cameraID);
                        StreamConfigurationMap map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
                        Size mSize = sizes[0];
                        int width = Math.max(cam_w, cam_h);
                        int heigth = Math.min(cam_w, cam_h);
                        if (mSize.getWidth() != width || mSize.getHeight() != heigth) {
                            int mSizeX = Math.abs((mSize.getWidth() * mSize.getHeight()) - (cam_w * heigth));
                            for (int i = 1; i < sizes.length; i++) {
                                Size size = sizes[i];
                                if (size.getWidth() == width && mSize.getHeight() == heigth) {
                                    mSize = size;
                                    break;
                                } else {
                                    int sizeX = Math.abs((size.getWidth() * size.getHeight()) - (width * heigth));
                                    if (sizeX < mSizeX) {
                                        mSizeX = sizeX;
                                        mSize = size;
                                    }
                                }
                            }
                        }
                        //FlyLog.d("Camera width=%d, height=%d", mSize.getWidth(), mSize.getHeight());
                        mBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        SurfaceTexture surfaceTexture1 = mEglGLSurface.getSurfaceTexture();
                        SurfaceTexture surfaceTexture2 = mTextureView.getSurfaceTexture();
                        surfaceTexture1.setDefaultBufferSize(mSize.getWidth(), mSize.getHeight());
                        surfaceTexture2.setDefaultBufferSize(mSize.getWidth(), mSize.getHeight());
                        Surface surface1 = new Surface(surfaceTexture1);
                        Surface surface2 = new Surface(surfaceTexture2);
                        mBuilder.addTarget(surface1);
                        mBuilder.addTarget(surface2);
                        mCameraDevice.createCaptureSession(
                                Arrays.asList(surface1, surface2),
                                new CameraCaptureSession.StateCallback() {
                                    @Override
                                    public void onConfigured(CameraCaptureSession session) {
                                        try {
                                            mCaptureSession = session;
                                            mCaptureSession.setRepeatingRequest(
                                                    mBuilder.build(),
                                                    new CameraCaptureSession.CaptureCallback() {
                                                        @Override
                                                        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
                                                            super.onCaptureProgressed(session, request, partialResult);
                                                        }

                                                        @Override
                                                        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                                            super.onCaptureCompleted(session, request, result);
                                                            mEglGLSurface.requestRender();
                                                        }
                                                    }, mCamBkHandler);
                                        } catch (CameraAccessException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onConfigureFailed(CameraCaptureSession session) {

                                    }
                                }, mCamBkHandler);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mEglGLSurface.create();
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    camera.close();
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    camera.close();
                }
            }, mCamBkHandler);
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }

        is_opened.set(true);

        wFrameThread = new Thread(() -> {
            while (is_opened.get()) {
                synchronized (mFrameLock) {
                    try {
                        mFrameLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!is_opened.get()) return;
                FlyYuv.ARGBToNV21(
                        frameRGBA.array(),
                        nv12,
                        0,
                        cam_w,
                        cam_h);
                for (IVideoListener listener : listeners) {
                    listener.notifyRGBFrame(nv12, nv12.length, cam_w, cam_h);
                }
            }
        }, "frame_thread");
        wFrameThread.start();
    }

    public void closeCamera() {
        is_opened.set(false);
        synchronized (mFrameLock) {
            mFrameLock.notifyAll();
        }
        try {
            wFrameThread.join();
            wFrameThread = null;
        } catch (InterruptedException e) {
            FlyLog.e(e.toString());
        }

        try {
            if (mCameraDevice != null) {
                mCameraDevice.close();
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
        mEglGLSurface.destory();
    }

    public void swapCamera() {
        closeCamera();
        cameraID = cameraID.endsWith("0") ? "1" : "0";
        SPUtil.set(mContext, CAMERA_ID_KEY, cameraID);
        openCamera();
    }

    public void upRenderData() {
        if (frameRGBA != null) {
            GLES30.glReadPixels(0, 0, cam_w, cam_h, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, frameRGBA);
            synchronized (mFrameLock) {
                mFrameLock.notify();
            }
        }
    }

    private final List<IVideoListener> listeners = new ArrayList<>();

    public void addFrameListener(IVideoListener listener) {
        listeners.add(listener);
    }

    public void removeFrameListener(IVideoListener listener) {
        listeners.remove(listener);
    }
}


