package com.also.steptwo_audiorecordaudiotrack.manager;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 描述：
 * 作者：ye.yuan
 * 邮箱：ye.yuan@lingware.cn
 * 创建时间：2019/2/27 11:02 PM
 */
public class RecordManager {

    private static RecordManager mInstance;
    private Context mContext;

    //音频源-音频输入 麦克风
    public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    // 采样率
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    public final static int AUDIO_SAMPLE_RATE = 16000;
    // 音频通道 单声道
    public final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    // 音频格式：PCM编码
    public final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区大小：缓冲区字节大小
    private int bufferSizeInBytes = 0;

    private static AudioRecord mAudioRecord;
    private String mFileName;
    private boolean isRecording;
    private SaveThread mThread;


    private RecordManager(Context context) {
        mContext = context;
    }

    public static RecordManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (RecordManager.class) {
                if (mInstance == null) {
                    mInstance = new RecordManager(context);
                }
            }
        }
        return mInstance;
    }

    public void createDefaultAudio(String fileName) {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL,
                AUDIO_ENCODING);
        mAudioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL,
                AUDIO_ENCODING, bufferSizeInBytes);
        mFileName = fileName;
    }

    public void startRecord() {
        if (mAudioRecord != null) {
            mAudioRecord.startRecording();
            isRecording = true;
            save2PCM();
        }

    }

    public void stopRecord() {
        isRecording = false;
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
            mThread.exit();
            mThread = null;
        }
    }

    private void save2PCM() {
        mThread = new SaveThread();
        mThread.start();
    }

    class SaveThread extends Thread{

        public void exit() {
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            FileOutputStream fos = null;
            byte[] temp = new byte[bufferSizeInBytes];
            try {
                fos = new FileOutputStream(mFileName);
                while(isRecording){
                    int read = mAudioRecord.read(temp, 0, bufferSizeInBytes);
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                       fos.write(temp);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("RecordTest", "保存完毕");
            }
        }
    }

    public int getBufferSizeInBytes() {
        return bufferSizeInBytes;
    }
}
