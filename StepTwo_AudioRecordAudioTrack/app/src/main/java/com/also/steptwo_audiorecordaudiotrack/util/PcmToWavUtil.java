package com.also.steptwo_audiorecordaudiotrack.util;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 描述：
 * 作者：ye.yuan
 * 邮箱：ye.yuan@lingware.cn
 * 创建时间：2019/2/27 11:39 PM
 */
public class PcmToWavUtil {

    /**
     * pcm文件转wav文件
     * @param sampleRate sample rate、采样率
     * @param channel channel、声道
     * @param encoding Audio data format、音频格式
     * @param inFilename 源文件路径
     * @param outFilename 目标文件路径
     */
    public static void pcmToWav(final Context context, final int sampleRate, final int channel, final int encoding, final String inFilename, final String outFilename) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                FileInputStream in;
                FileOutputStream out;
                long totalAudioLen;
                long totalDataLen;
                //采样率
                long longSampleRate = sampleRate;
                //声道数
                int channels = channel == AudioFormat.CHANNEL_IN_MONO ? 1 : 2;
                long byteRate = 16 * sampleRate * channels / 8;
                int mBufferSize = AudioRecord.getMinBufferSize(sampleRate, channel, encoding);
                byte[] data = new byte[mBufferSize];
                try {
                    in = new FileInputStream(inFilename);
                    out = new FileOutputStream(outFilename);
                    totalAudioLen = in.getChannel().size();
                    totalDataLen = totalAudioLen + 36;

                    writeWaveFileHeader(out, totalAudioLen, totalDataLen,
                            longSampleRate, channels, byteRate);
                    while (in.read(data) != -1) {
                        out.write(data);
                    }
                    Log.i("RecordTest", "转换完毕");
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    /**
     * 加入wav文件头
     * @param out
     * @param totalAudioLen 数据部分总长度（字节数）
     * @param totalDataLen 数据字节数
     * @param longSampleRate 采样频率
     * @param channels 声道数
     * @param byteRate 波形文件每秒的字节数=采样率*PCM位深/8*声道数
     * @throws IOException
     */
    private static void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                 /**/    long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        // RIFF/WAVE header
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        //WAVE
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        // 'fmt ' chunk
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        // 4 bytes: size of 'fmt ' chunk
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        // format = 1
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // block align
        header[32] = (byte) (2 * 16 / 8);
        header[33] = 0;
        // bits per sample
        header[34] = 16;
        header[35] = 0;
        //data
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
