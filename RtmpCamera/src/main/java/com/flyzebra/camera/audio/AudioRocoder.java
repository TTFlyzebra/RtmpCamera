/**
 * FileName: AudioRocoder
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/24 13:53
 * Description:
 */
package com.flyzebra.camera.audio;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;

import com.flyzebra.camera.Config;
import com.flyzebra.utils.FlyLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioRocoder implements Runnable {
    private AudioRecord mAudioRecord;
    private AtomicBoolean is_stop = new AtomicBoolean(true);
    private byte[] pcm = new byte[960];
    private Thread mRecordThread = null;
    private int sample;
    private int channel;
    private int format;

    public AudioRocoder(Context context, int sample, int channel, int format) {
        this.sample = sample;
        this.channel = channel;
        this.format = format;
        int bufferSize = AudioRecord.getMinBufferSize(sample, channel, format);
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            FlyLog.e("check audio record permission failed!");
            return;
        }
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sample, channel, format, bufferSize);
        mRecordThread = new Thread(this);
    }

    public void start() {
        is_stop.set(false);
        mRecordThread.start();
    }

    public void stop() {
        is_stop.set(true);
        try {
            mRecordThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        mAudioRecord.startRecording();
        while (!is_stop.get()) {
            int readSize = mAudioRecord.read(pcm, 0, 960);
            if (readSize <= 0) {
                FlyLog.e("Audio read mic buffer error, readSize=%d", readSize);
            } else {
                for (IAudioListener listener : listeners) {
                    //TODO::
                    listener.notifyPCMFrame(pcm, readSize, sample, Config.MIC_CHANNELS, Config.MIC_BIT_RATE);
                }
            }
        }
        mAudioRecord.stop();
    }

    private final List<IAudioListener> listeners = new ArrayList<>();

    public void addFrameListener(IAudioListener listener) {
        listeners.add(listener);
    }

    public void removeFrameListener(IAudioListener listener) {
        listeners.remove(listener);
    }
}
