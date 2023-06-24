package com.flyzebra.rtmp;

import com.flyzebra.utils.FlyLog;

/**
 * Created by lake on 16-3-30.
 */
public class RtmpDump {
    static {
        System.loadLibrary("rtmpdump");
    }

    private long pRtmpPointer = -1;

    public RtmpDump() {
    }

    public void init(String rtmp_url) {
        pRtmpPointer = _init(rtmp_url);
    }

    public void release() {
        long pointer = pRtmpPointer;
        pRtmpPointer = -1;
        _release(pointer);
    }

    public void sendSpsPps(byte[] sps, int spsLen, byte[] pps, int ppsLen) {
        if (pRtmpPointer == -1) return;
        _sendSpsPps(pRtmpPointer, sps, spsLen, pps, ppsLen);
    }

    public void sendVpsSpsPps(byte[] vsp, int vspLen, byte[] sps, int spsLen, byte[] pps, int ppsLen) {
        if (pRtmpPointer == -1) return;
        _sendVpsSpsPps(pRtmpPointer, vsp, vspLen, sps, spsLen, pps, ppsLen);
    }

    public void sendAvc(byte[] data, int size, long pts) {
        if (pRtmpPointer == -1) return;
        _sendAvc(pRtmpPointer, data, size, pts);
    }

    public void sendHevc(byte[] data, int size, long pts) {
        if (pRtmpPointer == -1) return;
        _sendHevc(pRtmpPointer, data, size, pts);
    }

    public void sendAacHead(byte[] head, int size) {
        if (pRtmpPointer == -1) return;
        _sendAacHead(pRtmpPointer, head, size);
    }

    public void sendAac(byte[] data, int size, long pts) {
        if (pRtmpPointer == -1) return;
        _sendAac(pRtmpPointer, data, size, pts);
    }

    public void onError(int errCode) {
        FlyLog.e("RtmpDump onError %d", errCode);
    }

    private native long _init(String url);

    private native void _release(long pRtmpPointer);

    private native void _sendSpsPps(long pRtmpPointer, byte[] sps, int spsLen, byte[] pps, int ppsLen);

    private native void _sendVpsSpsPps(long pRtmpPointer, byte[] vsp, int vspLen, byte[] sps, int spsLen, byte[] pps, int ppsLen);

    private native void _sendAvc(long pRtmpPointer, byte[] data, int size, long pts);

    private native void _sendHevc(long pRtmpPointer, byte[] data, int size, long pts);

    private native void _sendAacHead(long pRtmpPointer, byte[] head, int headLen);

    private native void _sendAac(long pRtmpPointer, byte[] data, int size, long pts);

}
