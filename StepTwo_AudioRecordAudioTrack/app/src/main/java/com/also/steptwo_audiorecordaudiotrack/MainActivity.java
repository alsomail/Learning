package com.also.steptwo_audiorecordaudiotrack;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.also.steptwo_audiorecordaudiotrack.manager.AudioTrackManager;
import com.also.steptwo_audiorecordaudiotrack.manager.RecordManager;
import com.also.steptwo_audiorecordaudiotrack.util.Pcm2WAV;
import com.also.steptwo_audiorecordaudiotrack.util.PcmToWavUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnRecord;
    private Button mBtnPlay;
    private RecordManager mRecordManager;
    private View mBtnStopRecord;
    private String mFilePath;
    private String mToPath;
    private AudioTrackManager mAudioTrackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mBtnRecord = findViewById(R.id.btn_record);
        mBtnStopRecord = findViewById(R.id.btn_stopRecord);
        mBtnPlay = findViewById(R.id.btn_play);

        mBtnRecord.setOnClickListener(this);
        mBtnStopRecord.setOnClickListener(this);
        mBtnPlay.setOnClickListener(this);

        mRecordManager = RecordManager.getInstance(this);
        mAudioTrackManager = AudioTrackManager.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_record:
                mFilePath = getExternalCacheDir().getPath() + "/" + System.currentTimeMillis();
                mToPath = mFilePath + ".wav";
                Log.i("RecordTest","FilePath="+mFilePath);
                deleteCache(getExternalCacheDir().getPath());
                mRecordManager.createDefaultAudio(mFilePath);
                mRecordManager.startRecord();
                break;
            case R.id.btn_stopRecord:
                mRecordManager.stopRecord();
                Pcm2WAV.pcm2Wav(mFilePath,mToPath,AudioTrackManager.AUDIO_SAMPLE_RATE,AudioTrackManager.AUDIO_CHANNEL,AudioTrackManager.AUDIO_ENCODING);
//                PcmToWavUtil.pcmToWav(this,RecordManager.AUDIO_SAMPLE_RATE,RecordManager.AUDIO_CHANNEL,RecordManager.AUDIO_ENCODING,mFilePath,mFilePath+".wav");
                break;
            case R.id.btn_play:
                mAudioTrackManager.play(mFilePath);
                break;
        }
    }

    private void deleteCache(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i=0;i<files.length;i++) {
                if (files[i].isFile()) {
                    files[i].delete();
                }
            }
        }
    }

}
