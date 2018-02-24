package com.example.leidong.guesssongs.utils;

import android.util.Log;

/**
 * Created by leidong on 2018/2/11
 */

public class MyLog {
    private static final boolean DEBUG = true;

    public int logLevel = 0;

    public static void d(String tag, String message){
        if(DEBUG){
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message){
        if(DEBUG){
            Log.e(tag, message);
        }
    }

    public static void w(String tag, String message){
        if(DEBUG){
            Log.w(tag, message);
        }
    }
}
