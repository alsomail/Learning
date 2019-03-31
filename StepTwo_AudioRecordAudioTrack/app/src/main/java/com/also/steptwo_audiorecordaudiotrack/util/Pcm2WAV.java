package com.also.steptwo_audiorecordaudiotrack.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 描述：
 * 作者：ye.yuan
 * 邮箱：ye.yuan@lingware.cn
 * 创建时间：2019/3/17 4:00 PM
 */
public class Pcm2WAV {


    public  static void pcm2Wav(final String filePath, final String toPath, final int longSampleRate, final int channels, final int encoding ){
        new Thread(){
            @Override
            public void run() {
                super.run();
                pcm2WavInThread(filePath,toPath, longSampleRate,channels,encoding);
            }
        }.start();
    }

    private static void pcm2WavInThread(String filePath,String toPath,int longSampleRate, int channels,int encoding ) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        int channel = channels == AudioFormat.CHANNEL_OUT_MONO ? 1 : 2;
        try {
            fis = new FileInputStream(filePath);
            fos = new FileOutputStream(toPath);

            long dataLen = fis.getChannel().size();
            long totalDataLen = dataLen + 36;

            int minBufferSize = AudioTrack.getMinBufferSize(longSampleRate, channels, encoding);
            byte[] temp = new byte[minBufferSize];

            addHeader(fos,totalDataLen,(long)longSampleRate,channel,dataLen);

            while ((fis.read(temp)!=-1)){
                fos.write(temp);
            }
            Log.i("RecordTest","转换完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
         地址	大小	类型	内容
         00H-03H	4	char*4	资源文件交换标志RIFF
         04H-07H	4	unsigned int	从下个地址开始到文件末尾的字节数
         08H-0BH	4	char*4	WAV文件标志WAVE
         0CH-0FH	4	char*4	波形文件标志fmt ，最后一位是0x20空格
         10H-13H	4	unsigned int	子Chunk的文件头大小，对于WAV这个子Chunk该值为0x10
         14H-15H	2	unsigned short	格式类型，值为1时，表示数据为线性PCM编码
         16H-17H	2	unsigned short	声道数
         18H-1BH	4	unsigned int	采样频率
         1CH-1FH	4	unsigned int	波形文件每秒的字节数=采样率*PCM位深/8*声道数
         20H-21H	2	unsigned short	DATA数据块单位长度=声道数*PCM位深/8
         22H-23H	2	unsigned short	PCM位深
         24H-27H	4	char*4	数据标志data
         28H-2BH	4	unsigned int	数据部分总长度（字节数）
     * @param totalDataLen 从下个地址开始到文件末尾的字节数（除开头RIFF以及它本身的4位之外的数据总长）
     * @param longSampleRate 采样率
     * @param channels    声道数
     * @param dataLen 数据部分总长（除头文件部分数据总长）totalDataLen=dataLen+36
     */
    private static void addHeader(FileOutputStream fileOutputStream,long totalDataLen,long longSampleRate, int channels,long dataLen) throws IOException {
        byte[] header = new byte[44];
        // 00H-03H	4	char*4	资源文件交换标志RIFF
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        // 04H-07H	4	unsigned int	从下个地址开始到文件末尾的字节数
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);;
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);;
        //08H-0BH	4	char*4	WAV文件标志WAVE
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //  0CH-0FH	4	char*4	波形文件标志fmt ，最后一位是0x20空格
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = 0x20;
        //10H-13H	4	unsigned int	子Chunk的文件头大小，对于WAV这个子Chunk该值为0x10
        header[16] = 0x10;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //  14H-15H	2	unsigned short	格式类型，值为1时，表示数据为线性PCM编码
        header[20] = 0x1;
        header[21] = 0;
        //  16H-17H	2	unsigned short	声道数
        header[22] = (byte) channels;
        header[23] = 0;
        //18H-1BH	4	unsigned int	采样频率
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);;
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);;
        //1CH-1FH	4	unsigned int	波形文件每秒的字节数=采样率*PCM位深/8*声道数
        long byteRate = longSampleRate * 16 / 8 * channels;
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 8) & 0xff);
        header[31] = (byte) ((byteRate >> 8) & 0xff);
        //20H-21H	2	unsigned short	DATA数据块单位长度=声道数*PCM位深/8
        int blockAlign = channels * 16 / 8;
        header[32] = (byte) (blockAlign & 0xff);
        header[33] = (byte) ((blockAlign >> 8) & 0xff);;
        //22H-23H	2	unsigned short	PCM位深
        header[34] = (byte)16;
        header[35] = 0;
        //24H-27H	4	char*4	数据标志data
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        //28H-2BH	4	unsigned int	数据部分总长度（字节数）
        header[40] = (byte) (dataLen & 0xff);
        header[41] = (byte) ((dataLen >> 8) & 0xff);
        header[42] = (byte) ((dataLen >> 16) & 0xff);
        header[43] = (byte) ((dataLen >> 24) & 0xff);

        fileOutputStream.write(header,0,header.length);
    }
}
