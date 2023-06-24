/**
 * FileName: Config
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/23 11:33
 * Description:
 */
package com.flyzebra.camera;

import android.media.MediaFormat;

public class Config {
    public static final String RTMP_KEY = "RTMP_KEY";
    public static final String RTMP_URL = "rtmps://192.168.1.88:1938/live/test";
    public static final String MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
    public static final int BIT_RATE = 20000000;
    public static final int CAM_W = 720;
    public static final int CAM_H = 1280;
}
