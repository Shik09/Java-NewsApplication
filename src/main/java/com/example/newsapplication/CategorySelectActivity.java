package com.example.newsapplication;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import java.util.ArrayList;
import java.util.List;

public class CategorySelectActivity extends AppCompatActivity{
    private static int NUM_PAGES=1;
    private Button but;
    private ArrayList<String> cate_remain;
    private ArrayList<String> cate_delete;

    public class Adapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<String> mItems;

        public Adapter(Context c,List<String> data) {
            super();

            mContext = c;

            mItems = new ArrayList<String>();
            for (String item : data){
                mItems.add(item);
            }
        }

        public void add(String s){
            mItems.add(s);
            notifyDataSetChanged();
        }

        public void remove(int position){
            if(position < mItems.size()){
                mItems.remove(position);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
            return position;
        }

        @Override
        public String getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public boolean isEmpty() {
            return mItems.isEmpty();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        android.R.layout.simple_list_item_1, parent, false);
            }

            TextView tv = (TextView) convertView;
            tv.setText(getItem(position));
            return convertView;
        }
    }

    private ListView mListViewup;
    private ListView mListViewdown;
    private Adapter mAdapterup;
    private Adapter mAdapterdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoryselect);
        cate_remain = new ArrayList<String>();
        cate_delete = new ArrayList<String>();
        cate_remain.addAll(((NewsApplication)getApplication()).newsCategoryList.categoryList);
        cate_delete.addAll(((NewsApplication)getApplication()).newsCategoryList.AbandonedcategoryList);

        but = (Button)findViewById(R.id.finish_button);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3=new Intent();
                cate_remain = mAdapterup.mItems;
                cate_delete = mAdapterdown.mItems;
                intent3.putStringArrayListExtra("remain",cate_remain);
                intent3.putStringArrayListExtra("delete",cate_delete);
                setResult(RESULT_OK,intent3);
                finish();
            }
        });

        mListViewup = (ListView)findViewById(R.id.category_listview_up);
        mListViewdown = (ListView)findViewById(R.id.category_listview_down);

        mAdapterup = new Adapter(this, cate_remain);
        mAdapterdown = new Adapter(this, cate_delete);

        mListViewup.setAdapter(mAdapterup);
        mListViewdown.setAdapter(mAdapterdown);
        mListViewup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){        // 获得ListView第一个View的position
                int firstVisiblePosition = mListViewup.getFirstVisiblePosition();        // 存储所有的Animator，利用AnimatorSet直接播放
                ArrayList<Animator> animators = new ArrayList<Animator>();
                // 获得要删除的View
                View itemToDelete = mListViewup.getChildAt(position - firstVisiblePosition);

                int viewHeight = itemToDelete.getHeight();
                int dividerHeight = mListViewup.getDividerHeight();

                ObjectAnimator hideAnimator = ObjectAnimator.ofFloat(itemToDelete, "alpha",1f, 0f);

                animators.add(hideAnimator);

                int delay = 0;
                int firstViewToMove = position + 1;
                for (int i=firstViewToMove;i < mListViewup.getChildCount(); ++i){
                    View viewToMove = mListViewup.getChildAt(i);

                    ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(viewToMove, "translationY", 0, -dividerHeight-viewHeight);
                    moveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    moveAnimator.setStartDelay(delay);

                    delay += 5;

                    animators.add(moveAnimator);
                }

                AnimatorSet set = new AnimatorSet();
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mAdapterdown.add(mAdapterup.mItems.get(position));
                        mAdapterup.remove(position);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // 动画结束后，恢复ListView所有子View的属性
                        for (int i=0;i<mListViewup.getChildCount();++i){
                            View v = mListViewup.getChildAt(i);

                            v.setAlpha(1f);
                            v.setTranslationY(0);
                        }

                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {}
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });

                set.playTogether(animators);
                set.start();
            }
        });

        mListViewdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){        // 获得ListView第一个View的position
                int firstVisiblePosition = mListViewdown.getFirstVisiblePosition();        // 存储所有的Animator，利用AnimatorSet直接播放
                ArrayList<Animator> animators = new ArrayList<Animator>();
                // 获得要删除的View
                View itemToDelete = mListViewdown.getChildAt(position - firstVisiblePosition);

                int viewHeight = itemToDelete.getHeight();
                int dividerHeight = mListViewdown.getDividerHeight();

                ObjectAnimator hideAnimator = ObjectAnimator.ofFloat(itemToDelete, "alpha",1f, 0f);

                animators.add(hideAnimator);

                int delay = 0;
                int firstViewToMove = position + 1;
                for (int i=firstViewToMove;i < mListViewdown.getChildCount(); ++i){
                    View viewToMove = mListViewdown.getChildAt(i);

                    ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(viewToMove, "translationY", 0, -dividerHeight-viewHeight);
                    moveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                    moveAnimator.setStartDelay(delay);

                    delay += 5;

                    animators.add(moveAnimator);
                }

                AnimatorSet set = new AnimatorSet();
                set.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mAdapterup.add(mAdapterdown.mItems.get(position));
                        mAdapterdown.remove(position);
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // 动画结束后，恢复ListView所有子View的属性
                        for (int i=0;i<mListViewdown.getChildCount();++i){
                            View v = mListViewdown.getChildAt(i);

                            v.setAlpha(1f);
                            v.setTranslationY(0);
                        }

                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {}
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });

                set.playTogether(animators);
                set.start();
            }
        });
    }


}
