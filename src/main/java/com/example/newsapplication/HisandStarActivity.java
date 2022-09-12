package com.example.newsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HisandStarActivity extends AppCompatActivity{
    private static int NUM_PAGES=1;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private List<NewsItem> list;
    private boolean HisorStar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        final Intent intent = getIntent();
        String params = intent.getStringExtra("operation");
        if(params.equals("history")) HisorStar = true;
        else HisorStar = false;

        viewPager = findViewById(R.id.searchresult_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }



    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Log.d("TAG", "His&Star createFragment() called");
            if(HisorStar)
            {list = ((NewsApplication)getApplication()).getAll();}
            else {list = ((NewsApplication)getApplication()).getAllStar();}
            return new NewsFragment(list);
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}
