package com.flyzebra.rtmp;

/**
 * Created by lake on 16-3-30.
 */
public class RtmpDump {

    static {
        System.loadLibrary("rtmpdump");
    }

    /**
     * @param url
     * @param isPublishMode
     * @return rtmpPointer ,pointer to native rtmp struct
     */
    public static native long open(String url, boolean isPublishMode);

    public static native int read(long rtmpPointer, byte[] data, int offset, int size);

    public static native int write(long rtmpPointer, byte[] data, int size, int type, int ts);

    public static native int close(long rtmpPointer);

}
