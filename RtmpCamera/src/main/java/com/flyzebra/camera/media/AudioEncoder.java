/**
 * FileName: AudioEncoder
 * Author: FlyZebra
 * Email:flycnzebra@gmail.com
 * Date: 2023/6/24 13:46
 * Description:
 */
package com.flyzebra.camera.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.flyzebra.utils.FlyLog;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioEncoder implements Runnable {
    private MediaCodec codec = null;
    private final Object codecLock = new Object();
    private final AtomicBoolean is_codec_init = new AtomicBoolean(false);
    private Thread mOutThread = null;
    private AudioEncoderCB mCallBack;

    public AudioEncoder(AudioEncoderCB cb) {
        mCallBack = cb;
    }

    public boolean isCodecInit() {
        return is_codec_init.get();
    }

    public void initCodec(String mimeType, int sample, int channels, int bitrate) {
        synchronized (codecLock) {
            try {
                MediaFormat audioFormat = new MediaFormat();
                audioFormat.setString(MediaFormat.KEY_MIME, mimeType);
                audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
                audioFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, sample);
                audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channels);
                audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
                audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, bitrate / 4);
                codec = MediaCodec.createEncoderByType(mimeType);
                codec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                codec.start();
                is_codec_init.set(true);
                mOutThread = new Thread(this);
                mOutThread.start();
            } catch (Exception e) {
                FlyLog.e(e.toString());
            }
        }
    }

    public void inPumData(byte[] data, int size, long pts) {
        synchronized (codecLock) {
            if (!is_codec_init.get() || data == null || size <= 0) return;
            int inIndex = codec.dequeueInputBuffer(200000);
            if (inIndex < 0) {
                FlyLog.e("AudioEncoder codec->dequeueInputBuffer inIdex=%d error!", inIndex);
                return;
            }

            ByteBuffer buffer = codec.getInputBuffer(inIndex);
            if (buffer == null) {
                FlyLog.e("AudioEncoder codec->getInputBuffer inIdex=%d error!", inIndex);
                return;
            }
            buffer.put(data, 0, size);
            codec.queueInputBuffer(inIndex, 0, size, pts, 0);
        }
    }

    public void releaseCodec() {
        synchronized (codecLock) {
            is_codec_init.set(false);
            try {
                mOutThread.join();
            } catch (InterruptedException e) {
                FlyLog.e(e.toString());
            }
            codec.stop();
            codec.release();
            codec = null;
        }
    }

    @Override
    public void run() {
        MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
        while (is_codec_init.get()) {
            int outputIndex = codec.dequeueOutputBuffer(mBufferInfo, 200000);
            switch (outputIndex) {
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    try {
                        MediaFormat format = codec.getOutputFormat();
                        ByteBuffer buffer = format.getByteBuffer("csd-0");
                        buffer.position(0);
                        int size = buffer.remaining();
                        byte[] head = new byte[size];
                        buffer.get(head, 0, size);
                        mCallBack.notifyAacHead(head, size);
                    } catch (Exception e) {
                        FlyLog.e(e.toString());
                    }
                    break;
                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                    break;
                default:
                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) == 0 && mBufferInfo.size != 0) {
                        ByteBuffer outputBuffer = codec.getOutputBuffer(outputIndex);
                        outputBuffer.position(mBufferInfo.offset);
                        int size = outputBuffer.remaining();
                        byte[] data = new byte[size];
                        outputBuffer.get(data, 0, size);
                        mCallBack.notifyAacData(data, size, mBufferInfo.presentationTimeUs);
                    }
                    codec.releaseOutputBuffer(outputIndex, false);
                    break;
            }
        }
    }
}
