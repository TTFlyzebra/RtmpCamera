package com.flyzebra.rtmp;

import com.flyzebra.utils.FlyLog;

public class RtmpFfmpeg {
    static {
        System.loadLibrary("rtmpffmpeg");
    }

    private long mPtr = -1;

    public RtmpFfmpeg() {
        FlyLog.d("RtmpPushHelper()");
    }

    public void start(String url) {
        mPtr = openRtmpPushUrl(url);
    }

    public void close() {
        if (mPtr > 0) {
            closeRtmpPush(mPtr);
        }
    }

    public void connected() {

    }

    public void disconnected() {

    }

    private native long openRtmpPushUrl(String url);

    private native int pushSpsPps(long id, byte[] sps, int spsLen, byte[] pps, int ppsLen);

    private native int pushSpsPpsVps(long id, byte[] sps, int spsLen, byte[] pps, int ppsLen, byte[] vps, int vpsLen);

    private native int pushVideoFrame(long id, byte[] data, int dataLen, boolean keyFrame, boolean isH264);

    private native int pushAudioHeader(long id, byte[] data, int dataLen);

    private native int pushAudioFrame(long id, byte[] data, int dataLen);

    private native void closeRtmpPush(long id);

}
