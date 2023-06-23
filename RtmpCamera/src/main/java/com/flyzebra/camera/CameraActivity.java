package com.flyzebra.camera;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.flyzebra.eglcamera.EglCamera;
import com.flyzebra.utils.SPUtil;

public class CameraActivity extends AppCompatActivity {
    private SurfaceView mSurfaceView;
    private EditText et_rtmpurl;
    private EglCamera mEglCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mSurfaceView = findViewById(R.id.ac_main_tuv);
        et_rtmpurl = findViewById(R.id.et_rtmpurl);
        et_rtmpurl.setText((String) SPUtil.get(this, "RTMP_URL", "rtmps://192.168.1.88:1938/live/test"));
        mEglCamera = new EglCamera(this, mSurfaceView);
    }

    public void switchCamera(View view) {
        if (mEglCamera != null) mEglCamera.swapCamera();
    }
}