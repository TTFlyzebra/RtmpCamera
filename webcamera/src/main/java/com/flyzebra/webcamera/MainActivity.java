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
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.flyzebra.media.VideoEncode;
import com.flyzebra.opengl.GlVideoView;
import com.flyzebra.utils.FlyLog;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private CameraManager mCameraManager;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private ImageReader mImageReader;
    private String cameraID = "0";
    private int width = 1280;
    private int height = 720;

    private TextureView mTextureView;
//    private GlVideoView glVideoView;

    private VideoEncode mediaEncoder;

    private static final HandlerThread mThread = new HandlerThread("bgHandler");

    static {
        mThread.start();
    }

    private Handler mBackgroundHandler = new Handler(mThread.getLooper());

    private CameraDevice.StateCallback deviceCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            try {
                mCameraDevice = cameraDevice;

                SurfaceTexture texture = mTextureView.getSurfaceTexture();
                Surface surface = new Surface(texture);

                mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                mPreviewRequestBuilder.set(CaptureRequest.JPEG_THUMBNAIL_SIZE, new Size(width,height));
                mPreviewRequestBuilder.addTarget(surface);
                mPreviewRequestBuilder.addTarget(mImageReader.getSurface());
                mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), mCaptureStateCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                FlyLog.e(e.toString());
            }

        }

        @Override
        public void onError(CameraDevice cameraDevice, int arg1) {
            cameraDevice.close();
            mCameraDevice=null;
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }
    };

    private CameraCaptureSession.StateCallback mCaptureStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            if (null == mCameraDevice) {
                return;
            }
            mCaptureSession = cameraCaptureSession;
            try {
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), null, mBackgroundHandler);
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

//        glVideoView = findViewById(R.id.ac_main_gl);

    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        closeCamera();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    private void openCamera() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            if(mediaEncoder==null){
                mediaEncoder = new VideoEncode();
            }
            mediaEncoder.start();
            mImageReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 2);
            mImageReader.setOnImageAvailableListener(new OnImageAvailableListenerImpl(), mBackgroundHandler);
            mCameraManager.openCamera(cameraID, deviceCallBack, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != mediaEncoder) {
            mediaEncoder.stop();
            mediaEncoder = null;
        }
        if (null != mCaptureSession) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    public void switchCamera(View view){
        closeCamera();
        cameraID = cameraID.equals("0")?"1":"0";
        openCamera();
    }

    public void pushCamera(View view) {
    }

    private class OnImageAvailableListenerImpl implements ImageReader.OnImageAvailableListener {
        private byte[] y;
        private byte[] u;
        private byte[] v;
        private ReentrantLock lock = new ReentrantLock();

        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireNextImage();
            if (image.getFormat() == ImageFormat.YUV_420_888) {
                Image.Plane[] planes = image.getPlanes();
                lock.lock();
                if (y == null) {
                    y = new byte[planes[0].getBuffer().limit() - planes[0].getBuffer().position()];
                    u = new byte[planes[1].getBuffer().limit() - planes[1].getBuffer().position()];
                    v = new byte[planes[2].getBuffer().limit() - planes[2].getBuffer().position()];
                }
                if (image.getPlanes()[0].getBuffer().remaining() == y.length) {
                    planes[0].getBuffer().get(y);
                    planes[1].getBuffer().get(u);
                    planes[2].getBuffer().get(v);
                }
                lock.unlock();
//                FlyLog.d("widht=%d, height=%d, yL=%d, uL=%d, vL=%d",image.getWidth(),image.getHeight(),y.length,u.length,v.length);
//                glVideoView.pushyuvdata(y,u,v);
                if(mediaEncoder!=null){
                    mediaEncoder.pushyuvdata(y,u,v);
                }
            }
            image.close();
        }
    }

}