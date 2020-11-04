package com.flyzebra.media;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

import com.flyzebra.rtmp.FlvRtmpClient;
import com.flyzebra.utils.FlyLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author FlyZebra
 * 2019/6/18 16:12
 * Describ:
 **/
public class MediaEncoder implements Runnable {
    private MediaCodec mediaCodec;

    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc"; // H.264 Advanced Video Coding
    private static final int TIMEOUT_US = 10000;

    private AtomicBoolean isStop = new AtomicBoolean(true);
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private MediaCodec.BufferInfo mOutBufferInfo = new MediaCodec.BufferInfo();
    private long startTime = 0;

    private static final HandlerThread sWorkerThread = new HandlerThread("send-video");

    static {
        sWorkerThread.start();
    }

    private static final Handler tHandler = new Handler(sWorkerThread.getLooper());

    public static MediaEncoder getInstance() {
        return VideoStreamHolder.sInstance;
    }

    private static class VideoStreamHolder {
        public static final MediaEncoder sInstance = new MediaEncoder();
    }

    public void start() {
        FlvRtmpClient.getInstance().open(FlvRtmpClient.RTMP_ADDR);
        isStop.set(false);
        initMediaCodec();
        tHandler.post(this);
    }

    @Override
    public void run() {
        FlyLog.d("send video task start!");
        while (isRunning.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isRunning.set(true);
        while (!isStop.get()) {
            int outputIndex = mediaCodec.dequeueOutputBuffer(mOutBufferInfo, TIMEOUT_US);
            switch (outputIndex) {
                case MediaCodec.INFO_TRY_AGAIN_LATER:
                    break;
                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                    FlvRtmpClient.getInstance().sendVideoSPS(mediaCodec.getOutputFormat());
                    break;
                default:
                    if (startTime == 0) {
                        startTime = mOutBufferInfo.presentationTimeUs / 1000;
                    }
                    if (mOutBufferInfo.flags != MediaCodec.BUFFER_FLAG_CODEC_CONFIG && mOutBufferInfo.size != 0) {
                        ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outputIndex);
                        outputBuffer.position(mOutBufferInfo.offset);
                        outputBuffer.limit(mOutBufferInfo.offset + mOutBufferInfo.size);
                        FlvRtmpClient.getInstance().sendVideoFrame(outputBuffer, mOutBufferInfo, (int) ((mOutBufferInfo.presentationTimeUs / 1000) - startTime));
                    }
                    mediaCodec.releaseOutputBuffer(outputIndex, false);
                    break;
            }
        }
        mediaCodec.stop();
        mediaCodec.release();
        mediaCodec = null;
        isRunning.set(false);
        FlyLog.d("send video task end!");
    }


    private void initMediaCodec() {
        try {
            MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, FlvRtmpClient.VIDEO_WIDTH, FlvRtmpClient.VIDEO_HEIGHT);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            format.setInteger(MediaFormat.KEY_BIT_RATE, FlvRtmpClient.VIDEO_BITRATE);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, FlvRtmpClient.VIDEO_FPS);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, FlvRtmpClient.VIDEO_IFRAME_INTERVAL);
            mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void pushyuvdata(byte[] yy, byte[] uu, byte[] vv) {
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
        ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
        if (inputBuffer == null) {
            FlyLog.e("getInputBuffer failed!");
            return;
        }
        inputBuffer.clear();
        int length = yy.length + uu.length / 2 + vv.length / 2;
        if (yy.length / uu.length == 2) {
            ((ByteBuffer) inputBuffer).put(yy, 0, yy.length);
            int uIndex = 0, vIndex = 0;
            for (int i = yy.length; i < length; i += 2) {
                ((ByteBuffer) inputBuffer).put(uu[uIndex]);
                uIndex += 2;
            }
            for (int i = yy.length; i < length; i += 2) {
                ((ByteBuffer) inputBuffer).put(vv[vIndex]);
                vIndex += 2;
            }
        }
        mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, SystemClock.uptimeMillis(), 0);
    }

    public void stop() {
        tHandler.removeCallbacksAndMessages(null);
        FlvRtmpClient.getInstance().close();
        isStop.set(true);
    }

}
