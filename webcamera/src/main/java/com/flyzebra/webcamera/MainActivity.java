package com.flyzebra.webcamera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private CameraManager mCameraManager;
    private TextureView mTextureView;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;
    private Surface mPreviewSurface;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private ImageReader mImageReader;

    private static final HandlerThread mThread = new HandlerThread("bgHandler");

    static {
        mThread.start();
    }

    private Handler mThreadHandler = new Handler(mThread.getLooper());

    private CameraDevice.StateCallback deviceCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice arg0) {
            try {
                mCameraDevice = arg0;
                mImageReader = ImageReader.newInstance(720, 1280, ImageFormat.YUV_420_888, 1);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);
                mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                mCaptureRequestBuilder.addTarget(mPreviewSurface);
                mCaptureRequestBuilder.addTarget(mImageReader.getSurface());
                mCameraDevice.createCaptureSession(Arrays.asList(mPreviewSurface,mImageReader.getSurface()), sessionCallback, mThreadHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(CameraDevice arg0, int arg1) {

        }

        @Override
        public void onDisconnected(CameraDevice arg0) {

        }
    };

    private CameraCaptureSession.StateCallback sessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession arg0) {
            mCameraCaptureSession = arg0;
            try {
                mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mThreadHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession arg0) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mTextureView = findViewById(R.id.ac_main_tuv);
        mTextureView.setSurfaceTextureListener(this);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        mPreviewSurface = new Surface(surfaceTexture);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            mCameraManager.openCamera("1", deviceCallBack, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
//            Image image = reader.acquireLatestImage();
//            if (image == null) {
//                return;
//            }
//            final Image.Plane[] planes = image.getPlanes();
//            int width = image.getWidth();
//            int height = image.getHeight();
//            // Y、U、V数据
//            byte[] yBytes = new byte[width * height];
//            byte uBytes[] = new byte[width * height / 4];
//            byte vBytes[] = new byte[width * height / 4];
//            //目标数组的装填到的位置
//            int dstIndex = 0;
//            int uIndex = 0;
//            int vIndex = 0;
//            int pixelsStride, rowStride;
//            for (int i = 0; i < planes.length; i++) {
//                pixelsStride = planes[i].getPixelStride();
//                rowStride = planes[i].getRowStride();
//                ByteBuffer buffer = planes[i].getBuffer();
//                byte[] bytes = new byte[buffer.capacity()];
//                buffer.get(bytes);
//                int srcIndex = 0;
//                if (i == 0) {
//                    //直接取出来所有Y的有效区域，也可以存储成一个临时的bytes，到下一步再copy
//                    for (int j = 0; j < height; j++) {
//                        System.arraycopy(bytes, srcIndex, yBytes, dstIndex, width);
//                        srcIndex += rowStride;
//                        dstIndex += width;
//                    }
//                } else if (i == 1) {
//                    //根据pixelsStride取相应的数据
//                    for (int j = 0; j < height / 2; j++) {
//                        for (int k = 0; k < width / 2; k++) {
//                            uBytes[uIndex++] = bytes[srcIndex];
//                            srcIndex += pixelsStride;
//                        }
//                        if (pixelsStride == 2) {
//                            srcIndex += rowStride - width;
//                        } else if (pixelsStride == 1) {
//                            srcIndex += rowStride - width / 2;
//                        }
//                    }
//                } else if (i == 2) {
//                    //根据pixelsStride取相应的数据
//                    for (int j = 0; j < height / 2; j++) {
//                        for (int k = 0; k < width / 2; k++) {
//                            vBytes[vIndex++] = bytes[srcIndex];
//                            srcIndex += pixelsStride;
//                        }
//                        if (pixelsStride == 2) {
//                            srcIndex += rowStride - width;
//                        } else if (pixelsStride == 1) {
//                            srcIndex += rowStride - width / 2;
//                        }
//                    }
//                }
//            }
            // 将YUV数据交给C层去处理。
        }

    };
}