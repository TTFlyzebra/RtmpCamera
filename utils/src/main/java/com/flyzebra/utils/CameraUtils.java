package com.flyzebra.utils;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Range;
import android.util.Size;

import java.util.Arrays;

public class CameraUtils {

    public static Size getCameraSize(Context context, int deviceWidth, int deviceHeigh) {
        Size findSize = null;
        try {
            CameraManager mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            for (final String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);

                Range<Integer>[] fpsRanges = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size[] sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
                for (int i = 0; i < sizes.length; i++) { //遍历所有Size
                    Size itemSize = sizes[i];
                    FlyLog.e("当前itemSize 宽=" + itemSize.getWidth() + "高=" + itemSize.getHeight());
                    if (itemSize.getHeight() < (deviceWidth) && itemSize.getHeight() > (deviceWidth)) {
                        if (findSize != null) { //如果之前已经找到一个匹配的宽度
                            if (Math.abs(deviceHeigh - itemSize.getWidth()) < Math.abs(deviceHeigh - findSize.getWidth())) { //求绝对值算出最接近设备高度的尺寸
                                findSize = itemSize;
                                continue;
                            }
                        } else {
                            findSize = itemSize;
                        }

                    }
                }
                if (findSize != null) {
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return findSize;
    }

    public static Range<Integer>[] getCameraFps(Context context) {
        Range<Integer>[] fpsRanges = null;
        try {
            CameraManager mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            for (final String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
                fpsRanges = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
                FlyLog.e("SYNC_MAX_LATENCY_PER_FRAME_CONTROL: " + Arrays.toString(fpsRanges));
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return fpsRanges;
    }
}
