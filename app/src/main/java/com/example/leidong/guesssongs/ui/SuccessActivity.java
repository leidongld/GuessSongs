package com.example.leidong.guesssongs.ui;

import android.app.Activity;
import android.os.Bundle;

import com.example.leidong.guesssongs.R;
import com.example.leidong.guesssongs.data.Constants;
import com.example.leidong.guesssongs.utils.StorageUtils;

/**
 * Created by leidong on 2018/2/22
 */

public class SuccessActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        StorageUtils.saveData(SuccessActivity.this, -1, Constants.TOTAL_COINS);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
