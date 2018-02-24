package com.example.leidong.guesssongs.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import com.example.leidong.guesssongs.data.Constants;

import java.io.IOException;

/**
 * Created by leidong on 2018/2/22
 */

public class MyPlayer {

    private static MediaPlayer mediaPlayer;
    
    //音效
    private static MediaPlayer[] voices = new MediaPlayer[Constants.VOICES_NAME.length];
    public static final int INDEX_TONE_ENTER = 0;
    public static final int INDEX_TONE_COINS = 1;
    public static final int INDEX_TONE_CANCEL = 2;

    /**
     * 播放歌曲
     * @param context
     * @param songName
     * @throws IOException
     */
    public static void playSong(Context context, String songName) throws IOException {
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        //强制重置
        mediaPlayer.reset();
        //加载声音
        AssetManager assetManager = context.getAssets();
        AssetFileDescriptor assetFileDescriptor = assetManager.openFd(songName);
        mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                assetFileDescriptor.getStartOffset(),
                assetFileDescriptor.getLength());
        mediaPlayer.prepare();
        //声音播放
        mediaPlayer.start();
    }

    /**
     * 停止歌曲
     * @param context
     */
    public static void stopSong(Context context){
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * 播放音效
     * @param context
     */
    public static void playTone(Context context, int index) throws IOException {
        if(voices[index] == null){
            voices[index] = new MediaPlayer();
        }
        //强制重置
        voices[index].reset();
        AssetManager assetManager = context.getAssets();
        AssetFileDescriptor assetFileDescriptor = assetManager.openFd(Constants.VOICES_NAME[index]);
        voices[index].setDataSource(assetFileDescriptor.getFileDescriptor(),
                assetFileDescriptor.getStartOffset(),
                assetFileDescriptor.getLength());
        voices[index].prepare();
        //声音播放
        voices[index].start();
    }
}
