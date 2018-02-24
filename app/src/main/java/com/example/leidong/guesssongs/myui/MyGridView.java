package com.example.leidong.guesssongs.myui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.example.leidong.guesssongs.R;
import com.example.leidong.guesssongs.model.IWordButtonClickListener;
import com.example.leidong.guesssongs.model.WordButton;
import com.example.leidong.guesssongs.utils.WordUtils;

import java.util.ArrayList;

/**
 * Created by leidong on 2018/2/5
 */

public class MyGridView extends GridView{
    public static final int WORD_COUNTS = 24;

    private MyGridAdapter mMyGridAdapter;
    private ArrayList<WordButton> wordList = new ArrayList<>();
    private Context mContext;

    private Animation scaleAnimation;

    private IWordButtonClickListener iWordButtonClickListener;

    public MyGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        mMyGridAdapter = new MyGridAdapter();
        this.setAdapter(mMyGridAdapter);
    }

    public void updateData(ArrayList<WordButton> wordList){
        this.wordList = wordList;
        setAdapter(mMyGridAdapter);
    }

    class MyGridAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return wordList.size();
        }

        @Override
        public Object getItem(int i) {
            return wordList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final WordButton holder;

            if(view == null){
                view = WordUtils.getView(mContext, R.layout.self_ui_gridview_item);
                holder = wordList.get(i);

                //加载动画
                scaleAnimation = AnimationUtils.loadAnimation(mContext ,R.anim.scale);
                scaleAnimation.setStartOffset(300);

//                holder.mIndex = i;
//                holder.mViewButton = view.findViewById(R.id.item_button);
                holder.setmIndex(i);
                holder.setmViewButton((Button) view.findViewById(R.id.item_button));
                view.findViewById(R.id.item_button).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iWordButtonClickListener.onWordButtonClick(holder);
                    }
                });
                view.setTag(holder);
            }
            else{
                holder = (WordButton) view.getTag();
            }
            holder.getmViewButton().setText(holder.getmWord());

            view.startAnimation(scaleAnimation);
            return view;
        }
    }

    /**
     * 注册监听接口
     * @param listener
     */
    public void registerOnWordButtonClick(IWordButtonClickListener listener){
        iWordButtonClickListener = listener;
    }
}
