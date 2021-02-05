package com.flyzebra.media;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
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
public class AudioStream {
    private String rtmpURL;
    private static final long WAIT_TIME = 5000;//1ms;
    private AtomicBoolean isStop = new AtomicBoolean(false);
    private AudioRecord mAudioRecord;
    private int recordBufSize = 0; // 声明recoordBufffer的大小字段
    private byte[] audioBuffer;
    private MediaCodec mAudioEncoder;
    private MediaFormat dstAudioFormat;
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    private static final HandlerThread sWorkerThread = new HandlerThread("encode-audio");

    static {
        sWorkerThread.start();
    }

    private static final Handler tHandler = new Handler(sWorkerThread.getLooper());

    private static final HandlerThread tWorkerThread = new HandlerThread("send-audio");

    static {
        tWorkerThread.start();
    }

    private static final Handler sHandler = new Handler(tWorkerThread.getLooper());
    private int startTime;

    private AtomicBoolean isRunning1 = new AtomicBoolean(false);
    private AtomicBoolean isRunning2 = new AtomicBoolean(false);

    public static AudioStream getInstance() {
        return AudioStreamHolder.sInstance;
    }

    private static class AudioStreamHolder {
        public static final AudioStream sInstance = new AudioStream();
    }

    private Runnable runPutTask = new Runnable() {
        @Override
        public void run() {
            FlyLog.d("record audio task start!");
            isRunning1.set(true);
            while (!isStop.get()) {
                int size = mAudioRecord.read(audioBuffer, 0, audioBuffer.length);
                if (size > 0) {
                    long nowTimeMs = SystemClock.uptimeMillis();
                    int eibIndex = mAudioEncoder.dequeueInputBuffer(20000);
                    if (eibIndex >= 0) {
                        ByteBuffer dstAudioEncoderIBuffer = mAudioEncoder.getInputBuffers()[eibIndex];
                        dstAudioEncoderIBuffer.position(0);
                        dstAudioEncoderIBuffer.put(audioBuffer, 0, audioBuffer.length);
                        mAudioEncoder.queueInputBuffer(eibIndex, 0, audioBuffer.length, nowTimeMs * 1000, 0);
                    } else {
                        //FlyLog.d("dstAudioEncoder.dequeueInputBuffer(-1)<0");
                    }
                }
            }
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
            mAudioEncoder.stop();
            mAudioEncoder.release();
            mAudioEncoder = null;
            isRunning1.set(false);
            FlyLog.d("record audio task end!");
        }
    };

    private Runnable runSendTask = new Runnable() {
        @Override
        public void run() {
            FlyLog.d("send audio task start!");
            isRunning2.set(true);
            while (!isStop.get()) {
                FlvRtmpClient.getInstance().open(rtmpURL);
                int ouputIndex = mAudioEncoder.dequeueOutputBuffer(mBufferInfo, WAIT_TIME);
                switch (ouputIndex) {
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
                        break;
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        FlvRtmpClient.getInstance().sendAudioSPS(mAudioEncoder.getOutputFormat());
                        break;
                    default:
                        //FlyLog.d("AudioSenderThread,MediaCode,eobIndex=" + ouputIndex);
                        if (startTime == 0) {
                            startTime = (int) (mBufferInfo.presentationTimeUs / 1000);
                        }
                        if (mBufferInfo.flags != MediaCodec.BUFFER_FLAG_CODEC_CONFIG && mBufferInfo.size != 0) {
                            ByteBuffer outputBuffer = mAudioEncoder.getOutputBuffers()[ouputIndex];
                            outputBuffer.position(mBufferInfo.offset);
                            outputBuffer.limit(mBufferInfo.offset + mBufferInfo.size);
                            FlvRtmpClient.getInstance().sendAudioFrame(outputBuffer, (int) (mBufferInfo.presentationTimeUs / 1000));
                            mBufferInfo.presentationTimeUs = getPTSUs();
                            prevOutputPTSUs = mBufferInfo.presentationTimeUs;
                        }
                        mAudioEncoder.releaseOutputBuffer(ouputIndex, false);
                        break;
                }
            }
            isRunning2.set(false);
            FlyLog.d("send audio task end!");
        }
    };

    private long prevOutputPTSUs;
    private long getPTSUs() {
        long result = System.nanoTime()/1000L;
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        return result;
    }


    public void start(String url) {
        while (isRunning1.get()||isRunning2.get()) {
            isStop.set(false);
            try {
                FlyLog.e("Thread is running!");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        rtmpURL = url;
        isStop.set(false);
        initAudioEncoder();
        initAudioRecord();
        tHandler.post(runPutTask);
        sHandler.post(runSendTask);
    }

    private void initAudioRecord() {
        if (mAudioRecord == null) {
            recordBufSize = AudioRecord.getMinBufferSize(FlvRtmpClient.AAC_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
            audioBuffer = new byte[recordBufSize];
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, FlvRtmpClient.AAC_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, recordBufSize);
            mAudioRecord.startRecording();
        }
    }

    private void initAudioEncoder() {
        if (mAudioEncoder == null) {
            dstAudioFormat = new MediaFormat();
            dstAudioFormat.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm");
            dstAudioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            dstAudioFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, FlvRtmpClient.AAC_SAMPLE_RATE);
            dstAudioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
            dstAudioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 32 * 1024);
            dstAudioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 8820);
            try {
                mAudioEncoder = MediaCodec.createEncoderByType(dstAudioFormat.getString(MediaFormat.KEY_MIME));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mAudioEncoder.configure(dstAudioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mAudioEncoder.start();
    }


    public void stop() {
        tHandler.removeCallbacksAndMessages(null);
        sHandler.removeCallbacksAndMessages(null);
        isStop.set(true);
        while (!isRunning1.get()||!isRunning2.get()){
            try {
                FlyLog.e("Thread don't exit!");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
