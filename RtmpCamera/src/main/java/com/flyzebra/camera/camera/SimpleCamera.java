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
import android.graphics.ImageFormat;
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
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.flyzebra.utils.FlyLog;
import com.flyzebra.utils.SPUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCamera {
    private Context mContext;
    private TextureView mTextureView;
    private int cam_w = 1280;
    private int cam_h = 720;
    private final String CAMERA_ID_KEY = "CAMERA_ID_KEY";
    public static String cameraID = "0";
    private CameraManager mCameraManager;
    private CaptureRequest.Builder mBuilder;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private HandlerThread mCamThread;
    private Handler mCamBkHandler;
    private ImageReader mImageReader = null;

    public SimpleCamera(Context context, TextureView textureView, int width, int height) {
        mContext = context;
        mTextureView = textureView;
        cam_w = width;
        cam_h = height;

        mCamThread = new HandlerThread("camera2");
        mCamThread.start();
        mCamBkHandler = new Handler(mCamThread.getLooper());
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        cameraID = (String) SPUtil.get(mContext, CAMERA_ID_KEY, "0");

    }

    public void openCamera() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            return;
        try {
            mImageReader = ImageReader.newInstance(cam_w, cam_h, ImageFormat.YUV_420_888, 1);
            mImageReader.setOnImageAvailableListener(new OnImageAvailableListenerImpl(cam_w, cam_h), mCamBkHandler);
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
                        SurfaceTexture surfaceTexture2 = mTextureView.getSurfaceTexture();
                        surfaceTexture2.setDefaultBufferSize(mSize.getWidth(), mSize.getHeight());
                        Surface surface1 = mImageReader.getSurface();
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
        if (mCameraDevice != null) {
            mCameraDevice.close();
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    public void swapCamera() {
        closeCamera();
        cameraID = cameraID.endsWith("0") ? "1" : "0";
        SPUtil.set(mContext, CAMERA_ID_KEY, cameraID);
        openCamera();
    }

    private class OnImageAvailableListenerImpl implements ImageReader.OnImageAvailableListener {
        private int width;
        private int height;
        private byte[] yuv;
        private final ReentrantLock lock = new ReentrantLock();

        public OnImageAvailableListenerImpl(int width, int height) {
            this.width = width;
            this.height = height;
            yuv = new byte[width * height * 3 / 2];
        }

        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireNextImage();
            if (image.getFormat() == ImageFormat.YUV_420_888) {
                Image.Plane[] planes = image.getPlanes();
                lock.lock();
                if (planes[1].getPixelStride() == 1) {
                    //yuv
                    int yLen = planes[0].getBuffer().remaining();
                    int uLen = planes[1].getBuffer().remaining();
                    int vLen = planes[2].getBuffer().remaining();
                    planes[0].getBuffer().get(yuv, 0, yLen);
                    planes[1].getBuffer().get(yuv, yLen, uLen);
                    planes[2].getBuffer().get(yuv, yLen + uLen, vLen);
                } else {
                    //nv12 or nv21
                    int yLen = planes[0].getBuffer().remaining();
                    int uLen = planes[1].getBuffer().remaining();
                    int vLen = planes[2].getBuffer().remaining();
                    planes[0].getBuffer().get(yuv, 0, yLen);
                    planes[1].getBuffer().get(yuv, yLen, uLen); //nv12
                    //planes[2].getBuffer().get(yuv, yLen, vLen); //nv21
                }
                for (IVideoListener listener : listeners) {
                    listener.notifyNv21Frame(yuv, width * height * 3 / 2, width, height);
                }
                lock.unlock();
            }
            image.close();
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


