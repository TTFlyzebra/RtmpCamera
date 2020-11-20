package com.flyzebra.rtmpcamera;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.flyzebra.media.AudioStream;
import com.flyzebra.media.VideoStream;
import com.flyzebra.rtmp.FlvRtmpClient;
import com.flyzebra.utils.CameraUtils;
import com.flyzebra.utils.FlyLog;
import com.flyzebra.utils.SPUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private String[] mPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private boolean isPermission = false;
    private boolean isSurfaceReady = false;
    private final int REQUEST_CODE = 102;

    private CameraManager mCameraManager;
    private CameraCaptureSession mCaptureSession;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private ImageReader mImageReader;
    private String cameraID = "1";

    private TextureView mTextureView;
    private EditText et_rtmpurl;
//    private GlVideoView glVideoView;

    private static final HandlerThread mThread = new HandlerThread("bgHandler");

    static {
        mThread.start();
    }

    private Handler mBackgroundHandler = new Handler(mThread.getLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        mTextureView = findViewById(R.id.ac_main_tuv);
        mTextureView.setSurfaceTextureListener(this);

        et_rtmpurl = findViewById(R.id.et_rtmpurl);
        et_rtmpurl.setText((String)SPUtil.get(this,"RTMP_URL","rtmp://192.168.1.88/live/flycam"));

        cameraID = (String) SPUtil.get(this,"CAMERAID","0");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            verifyPermissions();
        } else {
            isPermission = true;
        }
        FlyLog.d("isPermission="+isPermission);
//        glVideoView = findViewById(R.id.ac_main_gl);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            boolean authorized = true;
            for (int num : grantResults) {
                if (num != PackageManager.PERMISSION_GRANTED) {
                    authorized = false;
                    break;
                }
            }
            if (authorized) {
                isPermission = true;
                if(isSurfaceReady){
                    openCamera();
                }
            }
        }
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        if(isPermission){
            openCamera();
        }
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

    public void verifyPermissions() {
        List<String> applyPerms = new ArrayList<>();
        for (String permission : mPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                applyPerms.add(permission);
            }
        }
        if (!applyPerms.isEmpty()) {
            ActivityCompat.requestPermissions(this, applyPerms.toArray(new String[applyPerms.size()]), REQUEST_CODE);
        } else {
            isPermission = true;
        }
    }

    private void openCamera() {
        String url = et_rtmpurl.getText().toString();
        if(url.startsWith("rtmp://")){
            SPUtil.set(this,"RTMP_URL",url);
        }else{
            Toast.makeText(this,"rtmp url is error!",Toast.LENGTH_SHORT).show();
            return;
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                VideoStream.getInstance().start(url);
                AudioStream.getInstance().start(url);
                mImageReader = ImageReader.newInstance(FlvRtmpClient.VIDEO_WIDTH, FlvRtmpClient.VIDEO_HEIGHT, ImageFormat.YUV_420_888, 1);
                mImageReader.setOnImageAvailableListener(new OnImageAvailableListenerImpl(), mBackgroundHandler);
                mCameraManager.openCamera(cameraID, deviceCallBack, mBackgroundHandler);
            } catch (CameraAccessException e) {
                FlyLog.e(e.toString());
            }
        }
    }

    private void closeCamera() {
        AudioStream.getInstance().stop();
        VideoStream.getInstance().stop();
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

    public void switchCamera(View view) {
        closeCamera();
        cameraID = cameraID.equals("0") ? "1" : "0";
        SPUtil.set(this,"CAMERAID",cameraID);
        openCamera();
    }

    private CameraDevice.StateCallback deviceCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            try {
                mCameraDevice = cameraDevice;

                SurfaceTexture texture = mTextureView.getSurfaceTexture();
                texture.setDefaultBufferSize(FlvRtmpClient.VIDEO_WIDTH, FlvRtmpClient.VIDEO_HEIGHT);
                Surface surface = new Surface(texture);

                mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                mPreviewRequestBuilder.set(CaptureRequest.JPEG_THUMBNAIL_SIZE, new Size(FlvRtmpClient.VIDEO_WIDTH, FlvRtmpClient.VIDEO_HEIGHT));
                Range<Integer>[]  fpsRanges =  CameraUtils.getCameraFps(MainActivity.this);
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRanges[0]);
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
            mCameraDevice = null;
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
                FlyLog.e(e.toString());
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession arg0) {
        }
    };

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
                VideoStream.getInstance().pushyuvdata(y, u, v);
            }
            image.close();
        }
    }

}