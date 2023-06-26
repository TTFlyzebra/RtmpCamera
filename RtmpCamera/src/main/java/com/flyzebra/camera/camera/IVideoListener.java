package com.flyzebra.camera.camera;

public interface IVideoListener {
    void notifyNv21Frame(byte[] data, int size, int width, int heigth);
}
