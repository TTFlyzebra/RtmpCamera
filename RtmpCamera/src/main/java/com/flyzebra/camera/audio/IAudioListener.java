package com.flyzebra.camera.audio;

public interface IAudioListener {
    void notifyPCMFrame(byte[] data, int size, int sample, int channel, int format);
}
