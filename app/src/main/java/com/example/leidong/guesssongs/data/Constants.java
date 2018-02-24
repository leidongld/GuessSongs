package com.example.leidong.guesssongs.data;

/**
 * Created by leidong on 2018/2/10
 */

public class Constants {
    public static final String FILE_NAME_SAVE_DATA = "data.dat";
    public static final int INDEX_LAOD_DATA_STAGE = 0;
    public static final int INDEX_LOAD_DATA_COINS = 1;

    public static final String[][] SONG_INFO = {
            {"baiye.mp3", "白夜"},
            {"choubaguai.mp3", "丑八怪"},
            {"gangganghao.mp3", "刚刚好"},
            {"hongyanjiu.mp3", "红颜旧"},
            {"jinyifeiyu.mp3", "锦衣飞鱼"},
            {"paomo.mp3", "泡沫"},
            {"shanqiu.mp3", "山丘"},
            {"taheta.mp3", "他和她"},
            {"xiangsi.mp3", "相思"},
            {"xianggelila.mp3", "香格里拉"},
            {"suiyueshentou.mp3", "岁月神偷"}
    };

    public static final String[] VOICES_NAME = new String[]{
            "enter.mp3",
            "coins.mp3",
            "cancel.mp3"
    };

    public static final int SONG_NAME_INDEX = 1;
    public static final int SONG_FILE_NAME_INDEX = 0;
    public static final int WORDS_NUMBER = 24;
    public static final int SPARK_TIMES = 5;
    public static final int TOTAL_COINS = 1000;
}
