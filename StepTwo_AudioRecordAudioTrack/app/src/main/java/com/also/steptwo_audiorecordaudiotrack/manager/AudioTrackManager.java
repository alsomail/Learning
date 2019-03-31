package com.also.steptwo_audiorecordaudiotrack.manager;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.transition.Transition;
import android.util.Log;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 描述：
 * 作者：ye.yuan
 * 邮箱：ye.yuan@lingware.cn
 * 创建时间：2019/3/3 3:28 PM
 */
public class AudioTrackManager {

    // 采样率
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    public final static int AUDIO_SAMPLE_RATE = 16000;
    // 音频通道 单声道
    public final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_OUT_MONO;
    // 音频格式：PCM编码
    public final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区大小：缓冲区字节大小
    private int bufferSizeInBytes = 0;

    private static AudioTrackManager mInstance;

    private AudioTrack mAudioTrack;

    private boolean isPlay = false;

    private String mFileName;
    private Thread mPlayThread;

    private AudioTrackManager() {

    }

    public static AudioTrackManager getInstance() {
        if (mInstance == null) {
            synchronized (AudioTrackManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioTrackManager();
                }
            }
        }
        return mInstance;
    }

    private void init() {
        AudioTrack.Builder builder = new AudioTrack.Builder();
//
        AudioAttributes.Builder AudioAttributesBuilder = new AudioAttributes.Builder();
        AudioAttributesBuilder.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);
        AudioAttributesBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        builder.setAudioAttributes(AudioAttributesBuilder.build());
//
        AudioFormat.Builder AudioFormatBuilder = new AudioFormat.Builder();
        AudioFormatBuilder.setEncoding(AUDIO_ENCODING);
        AudioFormatBuilder.setSampleRate(AUDIO_SAMPLE_RATE);
        AudioFormatBuilder.setChannelMask(AUDIO_CHANNEL);
        builder.setAudioFormat(AudioFormatBuilder.build());
//
        bufferSizeInBytes = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL,
                AUDIO_ENCODING);
        builder.setBufferSizeInBytes(bufferSizeInBytes*2);
//
        builder.setTransferMode(AudioTrack.MODE_STREAM);

//        mAudioTrack= new AudioTrack(AudioAttributesBuilder.build(), AudioFormatBuilder.build(), bufferSizeInBytes,
//                AudioTrack.MODE_STREAM,AudioManager.AUDIO_SESSION_ID_GENERATE);
        mAudioTrack = builder.build();
        mAudioTrack.setVolume( 1.0f);

    }



    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAudioTrack != null) {
                mAudioTrack.stop();
                mAudioTrack.release();
                mAudioTrack = null;
            }
            init();
            DataInputStream dis = null;
            FileInputStream fis = null;
            try {
                dis = new DataInputStream(new FileInputStream(mFileName));
                fis = new FileInputStream(mFileName);
                byte[] temp = new byte[bufferSizeInBytes];
                int length = 0;

                while ((length = fis.read(temp,0,bufferSizeInBytes))!=-1) {
//                while (dis.available() > 0&&(length = dis.read(temp))!=-1) {
                    if (length!=AudioTrack.ERROR_BAD_VALUE&&length!=AudioTrack.ERROR_INVALID_OPERATION) {
                        Log.i("AudioTrack", "write");
                        mAudioTrack.play();
                        mAudioTrack.write(temp, 0, length);
                    }

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (mAudioTrack != null) {
                    if (mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                        mAudioTrack.stop();
                    }
                    mAudioTrack.release();
                    mAudioTrack = null;
                }
                if (dis != null) {
                    try {
                        dis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                isPlay = false;
                Log.i("AudioTrack", "播放结束");
            }
        }
    };

    public void play(String fileName) {
        if (isPlay) {
            return;
        }

        mFileName = fileName;
        if (mPlayThread != null) {
            try {
                mPlayThread.join();
                mPlayThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mPlayThread = new Thread(playRunnable);
        mPlayThread.start();

    }


}
