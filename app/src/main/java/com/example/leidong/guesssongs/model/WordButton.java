package com.example.leidong.guesssongs.model;

import android.widget.Button;

/**
 * 文字按钮
 * Created by leidong on 2018/2/5
 */

public class WordButton {
    private int mIndex;
    private boolean mIsVisible;
    private String mWord;
    private Button mViewButton;

    public WordButton(){
        mIsVisible = true;
        mWord = "";
    }

    public int getmIndex() {
        return mIndex;
    }

    public void setmIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public boolean ismIsVisible() {
        return mIsVisible;
    }

    public void setmIsVisible(boolean mIsVisible) {
        this.mIsVisible = mIsVisible;
    }

    public String getmWord() {
        return mWord;
    }

    public void setmWord(String mWord) {
        this.mWord = mWord;
    }

    public Button getmViewButton() {
        return mViewButton;
    }

    public void setmViewButton(Button mViewButton) {
        this.mViewButton = mViewButton;
    }
}
