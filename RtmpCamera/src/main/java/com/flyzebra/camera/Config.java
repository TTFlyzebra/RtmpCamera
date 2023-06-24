/**
 * FileName: Config
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/23 11:33
 * Description:
 */
package com.flyzebra.camera;

import android.media.AudioFormat;
import android.media.MediaFormat;

public class Config {
    public static final String RTMP_KEY = "RTMP_KEY";
    public static final String RTMP_URL = "rtmps://192.168.1.88:1938/live/test";

    public static final String CAM_MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
    public static final int CAM_W = 720;
    public static final int CAM_H = 1280;
    public static final int CAM_BIT_RATE = 80000000;

    public static final String MIC_MIME_TYPE = MediaFormat.MIMETYPE_AUDIO_AAC;
    public static final int MIC_SAMPLE = 44100;
    public static final int MIC_CHANNELS = 2;
    public static final int MIC_BIT_RATE = 32 * 1024;
    public static final int MIC_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int MIC_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
}
