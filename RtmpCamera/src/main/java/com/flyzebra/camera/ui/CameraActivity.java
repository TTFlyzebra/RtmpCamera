package com.flyzebra.camera.ui;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.flyzebra.camera.Config;
import com.flyzebra.camera.R;
import com.flyzebra.camera.service.RtmpusherService;
import com.flyzebra.eglcamera.EglCamera;
import com.flyzebra.eglcamera.IFrameListener;
import com.flyzebra.notify.Notify;
import com.flyzebra.notify.NotifyType;
import com.flyzebra.utils.ByteUtil;
import com.flyzebra.utils.SPUtil;

public class CameraActivity extends AppCompatActivity implements IFrameListener {
    private SurfaceView mSurfaceView;
    private EditText et_rtmpurl;
    private EglCamera mEglCamera;
    private RtmpusherService rtmpPushService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mSurfaceView = findViewById(R.id.ac_main_tuv);
        et_rtmpurl = findViewById(R.id.et_rtmpurl);
        et_rtmpurl.setText((String) SPUtil.get(this, Config.RTMP_KEY, Config.RTMP_URL));

        rtmpPushService = new RtmpusherService(Config.MIME_TYPE, Config.CAM_W, Config.CAM_H, Config.RTMP_URL);
        rtmpPushService.start();

        mEglCamera = new EglCamera(this, mSurfaceView, Config.CAM_W, Config.CAM_H);
        mEglCamera.addFrameListener(this);
    }

    @Override
    protected void onDestroy() {
        mEglCamera.removeFrameListener(this);

        rtmpPushService.stop();
        super.onDestroy();
    }

    public void switchCamera(View view) {
        if (mEglCamera != null) {
            SPUtil.set(this, Config.RTMP_KEY, et_rtmpurl.getText().toString());
            mEglCamera.swapCamera();
        }
    }

    @Override
    public void notifyRGBFrame(byte[] data, int size, int width, int heigth) {
        byte[] params = new byte[4];
        ByteUtil.shortToBytes((short) width, params, 0, true);
        ByteUtil.shortToBytes((short) heigth, params, 2, true);
        Notify.get().handledata(NotifyType.NOTI_CAMFIX_YUV, data, size, params);
    }
}