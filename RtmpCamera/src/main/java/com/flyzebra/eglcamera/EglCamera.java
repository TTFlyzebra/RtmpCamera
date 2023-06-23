/**
 * FileName: EglCamera
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/23 8:49
 * Description:可以通过OPENGL获取相机的原始RGB数据
 */
package com.flyzebra.eglcamera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.flyzebra.utils.FlyLog;
import com.flyzebra.utils.SPUtil;

import java.util.Collections;

public class EglCamera implements SurfaceHolder.Callback {
    private Context mContext;
    private CameraManager mCameraManager;
    private CaptureRequest.Builder mBuilder;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private String cameraID = "1";
    private HandlerThread mCamThread;
    private Handler mCamBkHandler;
    private SurfaceView mSurfaceView;
    private EglSurfaceView mEglView;
    private EglRender mEglRender;

    public EglCamera(Context context, SurfaceView surfaceView) {
        mContext = context;
        mSurfaceView = surfaceView;
        mSurfaceView.getHolder().addCallback(this);
        mCamThread = new HandlerThread("camera2 ");
        mCamThread.start();
        mCamBkHandler = new Handler(mCamThread.getLooper());
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        cameraID = (String) SPUtil.get(mContext, "CAMERA_ID", "1");
        mEglRender = new EglRender(context);
        mEglView = new EglSurfaceView(context);
        mEglView.init(mEglRender, mSurfaceView.getHolder());
    }

    public void openCamera() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            return;
        try {
            mCameraManager.openCamera(cameraID, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    try {
                        CameraCharacteristics c = mCameraManager.getCameraCharacteristics(cameraID);
                        StreamConfigurationMap map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
                        Size mSize = sizes[0];
                        if (mSize.getWidth() != 1280 || mSize.getHeight() != 720) {
                            int mSizeX = Math.abs((mSize.getWidth() * mSize.getHeight()) - (1280 * 720));
                            for (int i = 1; i < sizes.length; i++) {
                                Size size = sizes[i];
                                if (size.getWidth() == 1280 && mSize.getHeight() == 720) {
                                    mSize = size;
                                    break;
                                } else {
                                    int sizeX = Math.abs((size.getWidth() * size.getHeight()) - (1280 * 720));
                                    if (sizeX < mSizeX) {
                                        mSizeX = sizeX;
                                        mSize = size;
                                    }
                                }
                            }
                        }
                        mEglRender.getSurfaceTexture().setDefaultBufferSize(mSize.getWidth(), mSize.getHeight());
                        mCameraDevice = camera;
                        mBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        Surface surface = new Surface(mEglRender.getSurfaceTexture());
                        mBuilder.addTarget(surface);
                        mCameraDevice.createCaptureSession(
                                Collections.singletonList(surface),
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
                                                            mEglView.requestRender();
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
    }

    public void closeCamera() {
        try {
            if (mCameraDevice != null) {
                mCameraDevice.close();
            }
        } catch (Exception e) {
            FlyLog.e(e.toString());
        }
    }

    public void swapCamera() {
        closeCamera();
        cameraID = cameraID.endsWith("0") ? "1" : "0";
        SPUtil.set(mContext, "CAMERA_ID", cameraID);
        openCamera();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mEglView.surfaceCreated(holder);
        openCamera();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mEglView.surfaceChanged(holder, format, width, height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mEglView.surfaceDestroyed(holder);
        mEglRender.surfaceDestroyed(holder);
        closeCamera();
    }

}


