package com.flyzebra.camera.ui;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flyzebra.camera.Config;
import com.flyzebra.camera.R;
import com.flyzebra.camera.audio.AudioRocoder;
import com.flyzebra.camera.audio.IAudioListener;
import com.flyzebra.camera.camera.EglCamera;
import com.flyzebra.camera.camera.IVideoListener;
import com.flyzebra.camera.service.RtmpusherService;
import com.flyzebra.notify.Notify;
import com.flyzebra.notify.NotifyType;
import com.flyzebra.utils.ByteUtil;
import com.flyzebra.utils.FlyLog;
import com.flyzebra.utils.SPUtil;

public class CameraActivity extends AppCompatActivity implements
        TextureView.SurfaceTextureListener,
        IVideoListener,
        IAudioListener {
    private TextureView mTextureView;
    private EditText et_rtmpurl;
    private EglCamera mEglCamera;
    private AudioRocoder mAudioRocoder;
    private RtmpusherService rtmpPushService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mTextureView = findViewById(R.id.ac_main_tuv);
        et_rtmpurl = findViewById(R.id.et_rtmpurl);

        rtmpPushService = new RtmpusherService();
        String rtmp_rul = (String) SPUtil.get(this, Config.RTMP_KEY, Config.RTMP_URL);
        et_rtmpurl.setText(rtmp_rul);
        rtmpPushService.start(rtmp_rul);

        mEglCamera = new EglCamera(this, mTextureView, Config.CAM_W, Config.CAM_H);
        mEglCamera.addFrameListener(this);
        mAudioRocoder = new AudioRocoder(this, Config.MIC_SAMPLE, Config.MIC_CHANNEL, Config.MIC_FORMAT);
        mAudioRocoder.addFrameListener(this);

        mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    protected void onDestroy() {
        mEglCamera.removeFrameListener(this);
        mAudioRocoder.removeFrameListener(this);
        rtmpPushService.stop();
        super.onDestroy();
        FlyLog.d("RtmpCamera exit!");
    }

    public void switchCamera(View view) {
        if (mEglCamera != null) {
            String rtmp_url = et_rtmpurl.getText().toString();
            SPUtil.set(this, Config.RTMP_KEY, rtmp_url);
            mEglCamera.swapCamera();
            rtmpPushService.stop();
            rtmpPushService.start(rtmp_url);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        mEglCamera.openCamera();
        mAudioRocoder.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        mEglCamera.closeCamera();
        mAudioRocoder.stop();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }

    @Override
    public void notifyRGBFrame(byte[] data, int size, int width, int heigth) {
        byte[] params = new byte[4];
        ByteUtil.shortToBytes((short) width, params, 0, true);
        ByteUtil.shortToBytes((short) heigth, params, 2, true);
        Notify.get().handledata(NotifyType.NOTI_CAMFIX_YUV, data, size, params);
    }

    @Override
    public void notifyPCMFrame(byte[] data, int size, int sample, int channel, int format) {
        byte[] params = new byte[6];
        ByteUtil.shortToBytes((short) format, params, 0, true);
        ByteUtil.shortToBytes((short) sample, params, 2, true);
        ByteUtil.shortToBytes((short) channel, params, 4, true);
        Notify.get().handledata(NotifyType.NOTI_MICOUT_PCM, data, size, params);
    }
}