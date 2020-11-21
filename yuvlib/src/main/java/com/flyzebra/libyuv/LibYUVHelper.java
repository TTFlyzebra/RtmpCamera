package com.flyzebra.libyuv;

/**
 * Created by lake on 16-4-5.
 */
@SuppressWarnings("all")
public class LibYUVHelper {

    static {
        System.loadLibrary("flylibyuv");
    }

    static public native void NV21TOYUV420P(byte[] src, byte[]y, byte[] uv, byte[] dst, int widht, int height);

}
