package com.example.leidong.guesssongs.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.leidong.guesssongs.data.Constants;
import com.example.leidong.guesssongs.model.Song;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by leidong on 2018/2/5
 */

public class WordUtils {
    public static View getView(Context context, int layoutId){
        LayoutInflater layoutInflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(layoutId, null);
        return layout;
    }

    /**
     * 生成随机汉字
     * @return
     */
    private static char getRandomChar(){
        String s = "";
        int highPos;
        int lowPos;

        Random random = new Random();

        highPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(highPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            s = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s.charAt(0);
    }

    /**
     * 生成所有的待选文字
     * @return
     */
    public static String[] generateWords(Song song){
        String[] words = new String[Constants.WORDS_NUMBER];

        //存入歌名
        for(int i = 0; i < song.getSongLength(); i++){
            words[i] = song.getNameCharacters()[i] + "";
        }

        //存入随机生成的文字
        for(int i = song.getSongLength(); i < Constants.WORDS_NUMBER; i++){
            words[i] = getRandomChar() + "";
        }

        List<String> list = Arrays.asList(words);
        Collections.shuffle(list);

        for(int i = 0; i < Constants.WORDS_NUMBER; i++){
            words[i] = list.get(i);
        }
        return words;
    }
}
