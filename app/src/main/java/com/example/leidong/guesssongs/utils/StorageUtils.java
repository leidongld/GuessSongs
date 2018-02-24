package com.example.leidong.guesssongs.utils;

import android.content.Context;

import com.example.leidong.guesssongs.data.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by leidong on 2018/2/24
 */

public class StorageUtils {
    /**
     * 写入数据
     * @param context
     * @param stageIndex
     * @param coins
     */
    public static void saveData(Context context, int stageIndex, int coins){
        FileOutputStream fis = null;
        DataOutputStream dos = null;
        try {
            fis = context.openFileOutput(Constants.FILE_NAME_SAVE_DATA, Context.MODE_PRIVATE);
            dos = new DataOutputStream(fis);
            dos.writeInt(stageIndex);
            dos.writeInt(coins);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取数据
     * @param context
     * @return
     */
    public static int[] loadData(Context context){
        int[] params = {-1, Constants.TOTAL_COINS};
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(Constants.FILE_NAME_SAVE_DATA);
            DataInputStream dis = new DataInputStream(fis);
            params[Constants.INDEX_LAOD_DATA_STAGE] = dis.readInt();
            params[Constants.INDEX_LOAD_DATA_COINS] = dis.readInt();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return params;
        }
    }
}
