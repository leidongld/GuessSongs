package com.example.leidong.guesssongs.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.leidong.guesssongs.R;
import com.example.leidong.guesssongs.data.Constants;
import com.example.leidong.guesssongs.model.IWordButtonClickListener;
import com.example.leidong.guesssongs.model.Song;
import com.example.leidong.guesssongs.model.WordButton;
import com.example.leidong.guesssongs.myui.MyGridView;
import com.example.leidong.guesssongs.utils.MyLog;
import com.example.leidong.guesssongs.utils.MyPlayer;
import com.example.leidong.guesssongs.utils.StorageUtils;
import com.example.leidong.guesssongs.utils.WordUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IWordButtonClickListener{
    private static final String TAG = "MainActivity";

    public static final int STATUS_ANSWER_RIGHT = 1;
    public static final int STATUS_ANSWER_WRONG = 2;
    public static final int STATUS_ANSWER_LACK = 3;

    //返回按钮
    private ImageButton ibt_back;
    //分数按钮
    private ImageButton ibt_score;
    //播放按钮
    private ImageButton ibt_play;


    //唱片相关动画
    private Animation panAnimation;
    private LinearInterpolator panLinearInterpolator;

    //拨杆相关动画
    private Animation pinToLeftAnimation;
    private LinearInterpolator pinToLeftLinearInterpolator;

    private Animation pinToRightAnimation;
    private LinearInterpolator pinToRightLinearInterpolator;

    private ImageView iv_pan;
    private ImageView iv_pin;

    private boolean isRunning = false;

    private ArrayList<WordButton> mAllWords;
    private ArrayList<WordButton> mSelectWords;

    private MyGridView mMyGridView;
    private LinearLayout mViewWordsContainer;

    private Song currentSong;
    private int currentSongIndex = -1;

    private View mPassView;

    private int mCurrentCoins = Constants.TOTAL_COINS;

    private TextView mViewCurrentCoins;
    private TextView mCurrentStagePassView;
    private TextView mCurrentStageView;
    private TextView mCurrentSongNamePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int[] params = StorageUtils.loadData(MainActivity.this);
        currentSongIndex = params[Constants.INDEX_LAOD_DATA_STAGE];
        mCurrentCoins = params[Constants.INDEX_LOAD_DATA_COINS];

        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initAnimations();

        initWidgets();

        initActions();

        initCurrentStageData();

        mMyGridView.registerOnWordButtonClick(this);

        handleDeleteBt();
        handleTipBt();
        handleShareBt();
    }

    private Song loadStageSongInfo(int stageIndex){
        Song song = new Song();
        String[] stage = Constants.SONG_INFO[stageIndex];
        song.setSongName(stage[Constants.SONG_NAME_INDEX]);
        song.setSongFileName(stage[Constants.SONG_FILE_NAME_INDEX]);

        return song;
    }

    /**
     * 初始化当前关卡的文字数据
     */
    @SuppressLint("SetTextI18n")
    private void initCurrentStageData() {
        currentSong = loadStageSongInfo(++currentSongIndex);

        //初始化已选择框
        mSelectWords = initWordsSelect();

        //清空原来的答案
        mViewWordsContainer.removeAllViews();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
        for(int i = 0; i < mSelectWords.size(); i++){
            mViewWordsContainer.addView(mSelectWords.get(i).getmViewButton(), layoutParams);
        }

        //显示当前关卡的索引
        mCurrentStageView = findViewById(R.id.text_current_stage);
        if(mCurrentStageView != null) {
            mCurrentStageView.setText(currentSongIndex + 1 + "");
        }

        //获得数据
        mAllWords = initAllWords();

        //更新数据
        mMyGridView.updateData(mAllWords);

        //一开始就播放音乐
        try {
            clickPlayBt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化所有的待选文字
     * @return
     */
    private ArrayList<WordButton> initAllWords(){
        ArrayList<WordButton> wordList = new ArrayList<>();
        //获得所有待选文字
        String[] words = WordUtils.generateWords(currentSong);

        for(int i = 0; i < MyGridView.WORD_COUNTS; i++){
            WordButton wordButton = new WordButton();
            wordButton.setmIndex(i);
            //wordButton.setmIsVisible(true);
            wordButton.setmWord(words[i]);

            wordList.add(wordButton);
        }
        return wordList;
    }

    /**
     * 初始化已选文字框
     * @return
     */
    private ArrayList<WordButton> initWordsSelect(){
        ArrayList<WordButton> selectedWords = new ArrayList<>();

        for(int i = 0; i < currentSong.getSongLength(); i++){
            View view = WordUtils.getView(MainActivity.this, R.layout.self_ui_gridview_item);
            final WordButton wordButton = new WordButton();

            wordButton.setmViewButton((Button) view.findViewById(R.id.item_button));
            wordButton.getmViewButton().setTextColor(Color.WHITE);
            wordButton.setmWord("");
            wordButton.setmIsVisible(false);
            wordButton.getmViewButton().setBackgroundResource(R.drawable.game_wordblank);
            wordButton.getmViewButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearAnswer(wordButton);
                }
            });
            selectedWords.add(wordButton);
        }
        return selectedWords;
    }


    /**
     * 初始化动画
     */
    private void initAnimations() {
        //盘片动画初始化
        panAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        panLinearInterpolator = new LinearInterpolator();
        panAnimation.setFillAfter(true);
        panAnimation.setInterpolator(panLinearInterpolator);
        panAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isRunning = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isRunning = false;
                if(!isRunning) {
                    iv_pin.startAnimation(pinToRightAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //拨片向盘片移动
        pinToLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.pin_in);
        pinToLeftLinearInterpolator = new LinearInterpolator();
        pinToLeftAnimation.setFillAfter(true);
        pinToLeftAnimation.setInterpolator(pinToLeftLinearInterpolator);
        pinToLeftAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_pan.startAnimation(panAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //拨片向盘片远离
        pinToRightAnimation = AnimationUtils.loadAnimation(this, R.anim.pin_out);
        pinToRightLinearInterpolator = new LinearInterpolator();
        pinToRightAnimation.setFillAfter(true);
        pinToRightAnimation.setInterpolator(pinToRightLinearInterpolator);
        pinToRightAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ibt_play.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 初始化动作
     */
    private void initActions() {
        ibt_back.setOnClickListener(this);
        ibt_score.setOnClickListener(this);
        ibt_play.setOnClickListener(this);
    }

    /**
     * 初始化控件
     */
    @SuppressLint("SetTextI18n")
    private void initWidgets() {
        View topBarView = findViewById(R.id.top_bar_layout);
        ibt_back = topBarView.findViewById(R.id.ibt_back);
        ibt_score = topBarView.findViewById(R.id.ibt_score);
        mViewCurrentCoins = topBarView.findViewById(R.id.tv_score);
        mViewCurrentCoins.setText(mCurrentCoins+"");

        View panView = findViewById(R.id.pan_layout);
        ibt_play = panView.findViewById(R.id.ibt_play);

        iv_pan = panView.findViewById(R.id.iv_pan);
        iv_pin = panView.findViewById(R.id.iv_pin);

        View wordView = findViewById(R.id.name_select_layout);
        mMyGridView = wordView.findViewById(R.id.mygridview);
        mViewWordsContainer = wordView.findViewById(R.id.blanks);
    }



    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.ibt_back:
                clickBackBt();
                break;
            case R.id.ibt_score:
                clickScoreBt();
                break;
            case R.id.ibt_play:
                try {
                    clickPlayBt();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 删除
     */
    private void handleDeleteBt(){
        View floatView = findViewById(R.id.float_buttons);
        ImageButton ibt_delete = floatView.findViewById(R.id.ibt_delete);
        ibt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteOneWord();
            }
        });
    }

    /**
     * 提示
     */
    private void handleTipBt(){
        View floatView = findViewById(R.id.float_buttons);
        ImageButton ibt_tip = floatView.findViewById(R.id.ibt_tip);
        ibt_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipAnswer();
            }
        });
    }

    /**
     * 分享
     */
    private void handleShareBt(){
        View floatView = findViewById(R.id.float_buttons);
        ImageButton ibt_share = floatView.findViewById(R.id.ibt_share);
        ibt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /**
     * 增加或者减少指定数量的金币
     * @param num
     * @return
     */
    private boolean handleCoins(int num){
        if(mCurrentCoins + num >= 0){
            mCurrentCoins += num;
            mViewCurrentCoins.setText(mCurrentCoins + "");
            return true;
        }
        return false;
    }

    /**
     * 从配置文件里读取删除错误答案的金币数
     * @return
     */
    private int getDeleteWordCoins(){
        return this.getResources().getInteger(R.integer.pay_delete_word);
    }

    /**
     * 从配置文件里读取提示的金币数
     * @return
     */
    private int getTipAnswer(){
        return this.getResources().getInteger(R.integer.pay_tip_answer);
    }

    /**
     * 删除一个文字
     */
    private void deleteOneWord(){
        //金币够
        if(handleCoins(-getDeleteWordCoins())){
            setButtonVisible(findNotAnswerWord(), View.INVISIBLE);
        }
        //金币不够
        else{
            Toast.makeText(MainActivity.this, "金币不足!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 找到一个不是答案的文字按键，并且当前是可见的
     * @return
     */
    private WordButton findNotAnswerWord(){
        Random random = new Random();
        WordButton wordButton;
        while(true){
            int index = random.nextInt(Constants.WORDS_NUMBER);
            wordButton = mAllWords.get(index);
            if(wordButton.ismIsVisible() && !isAnswerWord(wordButton)){
                return wordButton;
            }
        }
    }

    /**
     * 找到一个答案文字
     * @return
     */
    private WordButton findIsAnswerWord(int index){
        WordButton wordButton;
        for(int i = 0; i < Constants.WORDS_NUMBER; i++){
            wordButton = mAllWords.get(i);
            if(wordButton.getmWord().equals(currentSong.getSongName().charAt(index)+"")){
                return wordButton;
            }
        }
        return null;
    }

    /**
     * 判断某个文字是否是答案
     * @param wordButton
     * @return
     */
    private boolean isAnswerWord(WordButton wordButton) {
        boolean res = false;
        for(int i = 0; i < currentSong.getSongName().length(); i++){
            if((currentSong.getSongName().charAt(i)+"").equals(wordButton.getmWord())){
                res = true;
                break;
            }
        }
        return res;
    }

    /**
     * 提示一个文字
     */
    private void tipAnswer(){
        if(handleCoins(-getTipAnswer())){
            boolean tipWord = false;
            for(int i = 0; i < mSelectWords.size(); i++){
                if(mSelectWords.get(i).getmWord().length() == 0){
                    //根据当前答案狂的条件选择对应的文字并且填入
                    onWordButtonClick(findIsAnswerWord(i));
                    tipWord = true;

                    //减少金币数量
                    if(!handleCoins(-getTipAnswer())){
                        return ;
                    }

                    break;
                }
            }
            if(!tipWord){
                sparkWords();
            }
        }
        else{
            Toast.makeText(MainActivity.this, "金币不足！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 点击播放按钮
     */
    private void clickPlayBt() throws IOException {
        //Toast.makeText(this, "clickPlayBt", Toast.LENGTH_LONG).show();
        if(!isRunning) {
            handlePlayAnimation();
            MyPlayer.playSong(MainActivity.this, currentSong.getSongFileName());
        }
        else{
            MyPlayer.stopSong(MainActivity.this);
        }
    }

    /**
     * 控制播放音乐的动画
     */
    private void handlePlayAnimation() {
        ibt_play.setVisibility(View.INVISIBLE);
        iv_pin.startAnimation(pinToLeftAnimation);
    }

    /**
     * 点击分数按钮
     */
    private void clickScoreBt() {
        //Toast.makeText(MainActivity.this, "clickScoreBt", Toast.LENGTH_LONG).show();
    }

    /**
     * 点击返回按钮
     */
    private void clickBackBt() {
        //Toast.makeText(MainActivity.this, "clickBackBt", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause(){
        super.onPause();
        iv_pan.clearAnimation();
        iv_pin.clearAnimation();

        MyPlayer.stopSong(MainActivity.this);

        StorageUtils.saveData(MainActivity.this, currentSongIndex-1, mCurrentCoins);
    }

    @Override
    public void onWordButtonClick(WordButton wordButton) {
        setSelectWord(wordButton);

        int checkRes = checkAnswer();

        switch (checkRes){
            case STATUS_ANSWER_RIGHT:
                for(int i = 0; i < mSelectWords.size(); i++){
                    mSelectWords.get(i).getmViewButton().setTextColor(Color.GREEN);
                }
                handlePassEvent();
                break;
            case STATUS_ANSWER_WRONG:
                sparkWords();
                for(int i = 0; i < mSelectWords.size(); i++){
                    mSelectWords.get(i).getmViewButton().setTextColor(Color.RED);
                }
                break;
            case STATUS_ANSWER_LACK:
                break;
            default:
                break;
        }
    }

    /**
     * 处理过关界面
     */
    @SuppressLint("SetTextI18n")
    private void handlePassEvent(){
        //停止正在播放的音乐
        MyPlayer.stopSong(MainActivity.this);

        //显示过关界面
        mPassView = findViewById(R.id.answer_right);
        mPassView.setVisibility(View.VISIBLE);

        final LinearLayout baseView = findViewById(R.id.base_view);
        baseView.setVisibility(View.GONE);

        final RelativeLayout floatView = findViewById(R.id.float_buttons);
        floatView.setVisibility(View.GONE);


        //停止未完成的动画
        iv_pan.clearAnimation();

        //当前关卡的索引
        mCurrentStagePassView = mPassView.findViewById(R.id.level_num);
        if(mCurrentStagePassView != null){
            mCurrentStagePassView.setText(currentSongIndex + 1 + "");
        }
        mCurrentSongNamePass = mPassView.findViewById(R.id.song);
        if(mCurrentSongNamePass != null){
            mCurrentSongNamePass.setText(currentSong.getSongName());
        }

        //下一关按键处理
        ImageButton ibt_pass_next = mPassView.findViewById(R.id.ibt_pass_next);
        ibt_pass_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(judgeAppPassed()){
                    //进入到通关界面
                    Intent intent = new Intent(MainActivity.this, SuccessActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    //进入下一题
                    mPassView.setVisibility(View.GONE);
                    floatView.setVisibility(View.VISIBLE);
                    baseView.setVisibility(View.VISIBLE);
                    //加载关卡数据
                    initCurrentStageData();
                }
            }
        });

        //分享按钮处理
        ImageButton ibt_pass_share = mPassView.findViewById(R.id.ibt_pass_share);
        ibt_pass_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /**
     * 是否已经通关
     * @return
     */
    private boolean judgeAppPassed(){
        return currentSongIndex == Constants.SONG_INFO.length - 1;
    }

    /**
     * 设置答案
     * @param wordButton
     */
    private void setSelectWord(WordButton wordButton){
        for(int i = 0; i < mSelectWords.size(); i++){
            if(mSelectWords.get(i).getmWord().length() == 0) {
                mSelectWords.get(i).getmViewButton().setText(wordButton.getmWord());
                mSelectWords.get(i).setmIsVisible(true);
                mSelectWords.get(i).setmWord(wordButton.getmWord());
                mSelectWords.get(i).setmIndex(wordButton.getmIndex());

                MyLog.d(TAG, "" + mSelectWords.get(i).getmIndex());

                //设置待选框可见性
                setButtonVisible(wordButton, View.INVISIBLE);
                break;
            }
        }
    }

    /**
     * 设置待选文字框是否可见
     * @param wordButton
     * @param visibility
     */
    private void setButtonVisible(WordButton wordButton, int visibility){
        wordButton.getmViewButton().setVisibility(visibility);
        wordButton.setmIsVisible(visibility == View.VISIBLE);
    }

    /**
     * 清除答案
     * @param wordButton
     */
    private void clearAnswer(WordButton wordButton){
        wordButton.getmViewButton().setText("");
        wordButton.setmWord("");

        setButtonVisible(mAllWords.get(wordButton.getmIndex()), View.VISIBLE);
    }

    private int checkAnswer(){
        //先检测长度
        for(int i = 0; i < mSelectWords.size(); i++){
            if(mSelectWords.get(i).getmWord().length() == 0){
                return STATUS_ANSWER_LACK;
            }
        }

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < mSelectWords.size(); i++){
            sb.append(mSelectWords.get(i).getmWord());
        }
        return sb.toString().equals(currentSong.getSongName()) ? STATUS_ANSWER_RIGHT : STATUS_ANSWER_WRONG;
    }

    /**
     *
     */
    private void sparkWords(){
        TimerTask timerTask = new TimerTask() {
            boolean mChange = true;
            int mSpardTime = 0;

            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    public void run(){
                        if(++mSpardTime > Constants.SPARK_TIMES){
                            return;
                        }

                        //交替显示红色和白色文字
                        for(int i = 0; i < mSelectWords.size(); i++){
                            mSelectWords.get(i).getmViewButton().setTextColor(
                                    mChange ? Color.RED : Color.WHITE
                            );
                        }

                        mChange = !mChange;
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 1, 200);
    }

    public void onDestroy(){
        super.onDestroy();
        //保存游戏数据

    }
}
