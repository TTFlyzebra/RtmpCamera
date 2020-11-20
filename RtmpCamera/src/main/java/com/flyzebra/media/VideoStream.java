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
public class VideoStream implements Runnable {
    private String rtmpURL;
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

    private final Object mLock = new Object();
    private ByteBuffer yuvBuffer = ByteBuffer.allocateDirect(1280 * 720 * 3 / 2 * 10);
    private byte[] sendData = new byte[1280 * 720 * 3 / 2];

    public static VideoStream getInstance() {
        return VideoStreamHolder.sInstance;
    }

    private VideoStream(){

    }

    private static class VideoStreamHolder {
        public static final VideoStream sInstance = new VideoStream();
    }

    public void start(String url) {
        while (isRunning.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        rtmpURL = url;
        isStop.set(false);
        initMediaCodec();
        tHandler.post(this);
    }

    @Override
    public void run() {
        FlyLog.d("send video task start!");
        isRunning.set(true);
        FlvRtmpClient.getInstance().open(rtmpURL);
        while (!isStop.get()) {
            boolean flag = false;
            synchronized (mLock) {
                if (yuvBuffer.position() > sendData.length) {
                    flag = true;
                    yuvBuffer.flip();
                    yuvBuffer.get(sendData);
                    yuvBuffer.compact();
                }
            }
            if (flag) {
                int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
                ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
                if (inputBuffer == null) {
                    FlyLog.e("getInputBuffer failed!");
                    return;
                }
                inputBuffer.clear();
                inputBuffer.put(sendData);
                if (firstTime == 0) {
                    firstTime = SystemClock.uptimeMillis() * 1000;
                }
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, sendData.length, SystemClock.uptimeMillis()*1000-firstTime, 0);
            }
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
        FlvRtmpClient.getInstance().close();
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

    long firstTime = 0;

    public void pushyuvdata(byte[] yy, byte[] uu, byte[] vv) {
        synchronized (mLock) {
            if (yuvBuffer.remaining() < ((int) yy.length * 1.5)) {
                //FlyLog.e("buffer is full");
                //yuvBuffer.clear();
            } else {
                int length = yy.length + uu.length / 2 + vv.length / 2;
                if (yy.length / uu.length == 2) {
                    ((ByteBuffer) yuvBuffer).put(yy, 0, yy.length);
                    int uIndex = 0, vIndex = 0;
                    for (int i = yy.length; i < length; i += 2) {
                        ((ByteBuffer) yuvBuffer).put(uu[uIndex]);
                        ((ByteBuffer) yuvBuffer).put(vv[uIndex]);
                        uIndex += 2;
                    }
                    ((ByteBuffer) yuvBuffer).put(uu[uu.length-1]);
//                    for (int i = yy.length; i < length; i += 2) {
//                        ((ByteBuffer) yuvBuffer).put(vv[vIndex]);
//                        vIndex += 2;
//                    }
                    ((ByteBuffer) yuvBuffer).put(vv[vv.length-1]);
                }
            }
        }
    }

    public void stop() {
        tHandler.removeCallbacksAndMessages(null);
        isStop.set(true);
        while (!isRunning.get()){
            try {
                FlyLog.e("Thread don't exit!");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
