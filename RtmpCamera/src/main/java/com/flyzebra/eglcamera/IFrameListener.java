package com.flyzebra.eglcamera;

public interface IFrameListener {
    void notifyRGBFrame(byte[] data, int size, int width, int heigth);
}
