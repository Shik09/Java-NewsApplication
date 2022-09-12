package com.example.newsapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static int NUM_PAGES = 10;
    private Toolbar head;
    private HorizontalScrollView horizontal_menu;
    private LinearLayout linear;
    private List<String> titles;
    private ArrayList<TextView> titlesView;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    private SearchView search_enter;

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_star:{
                    Intent intent = new Intent(MainActivity.this,HisandStarActivity.class);
                    intent.putExtra("operation","favorite");
                    MainActivity.this.startActivity(intent);
                    break;
                }
                case R.id.action_history:{
                    Intent intent = new Intent(MainActivity.this,HisandStarActivity.class);
                    intent.putExtra("operation","history");
                    MainActivity.this.startActivity(intent);
                    break;
                }
                case R.id.action_cate: {
                    Intent intent = new Intent(MainActivity.this, CategorySelectActivity.class);
                    Log.d("TAG", "onMenuItemClick: ");
                    MainActivity.this.startActivityForResult(intent,1);
                    break;
                }
            }

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        head = (Toolbar)findViewById(R.id.toolbar);
        head.inflateMenu(R.menu.menu_toolbar);
        head.setOnMenuItemClickListener(onMenuItemClick);

        search_enter = (SearchView)findViewById(R.id.search_view);
        search_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchPageActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        horizontal_menu = (HorizontalScrollView)findViewById(R.id.horizontal_scroll);
        linear = (LinearLayout)findViewById(R.id.category_menu_innerview);

        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        titles = ((NewsApplication)getApplicationContext()).newsCategoryList.categoryList;
        if(titles == null) {
            Log.d("TAG", "onCreate: titles is null");
        }
        titlesView = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            TextView textView = new TextView(MainActivity.this);
            textView.setTextSize(35);
            if (i == 0) {
                textView.setTextColor(Color.RED);
            }
            textView.setText(titles.get(i));
            textView.setId(i);//把循环的i设置给textview的下标;
            textView.setOnClickListener(this);

                    //LinearLayout中的孩子的定位参数
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(10,10,10,10);//设置左上右下四个margin值;
            //layoutParams是让linearLayout知道如何摆放自己孩子的位置的;
            linear.addView(textView,layoutParams);
            titlesView.add(textView);

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
                //当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直被调用。
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }
                // 此方法是页面跳转完后被调用
                @Override
                public void onPageSelected(int position) {
                    // 标题变色,用循环改变标题颜色,通过判断来决定谁红谁灰;
                    // 举例:娱乐的下标是position是0
                    for (int i = 0; i < titles.size(); i++) {
                        if(i == position){
                            titlesView.get(i).setTextColor(Color.RED);
                        }else {
                            titlesView.get(i).setTextColor(Color.GRAY);
                        }

                    }
                    // 标题滑动功能
                    int width = titlesView.get(position).getWidth();
                    int totalWidth = (width +20)*position;
                    horizontal_menu.scrollTo(totalWidth,0);
                }

                // 此方法是在状态改变的时候调用。
                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult: receive result");
        ArrayList<String> remain = data.getStringArrayListExtra("remain");
        ArrayList<String> delete = data.getStringArrayListExtra("delete");
        linear.removeAllViews();
        titlesView = new ArrayList<>();
        titles = remain;
        for (int i = 0; i < titles.size(); i++) {
            TextView textView = new TextView(MainActivity.this);
            textView.setTextSize(35);
            if (i == 0) {
                textView.setTextColor(Color.RED);
            }
            textView.setText(titles.get(i));
            textView.setId(i);//把循环的i设置给textview的下标;
            textView.setOnClickListener(this);

            //LinearLayout中的孩子的定位参数
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(10, 10, 10, 10);//设置左上右下四个margin值;
            //layoutParams是让linearLayout知道如何摆放自己孩子的位置的;
            linear.addView(textView, layoutParams);
            titlesView.add(textView);
        }

        ((NewsApplication)getApplication()).addCategorytoDB(remain,delete);

    }


    @Override
    public void onClick(View V)
    {
        int ID = V.getId();
        viewPager.setCurrentItem(ID);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Log.d("TAG", "createFragment() called with: position = [" + position + "]");
            return new NewsFragment(titles.get(position));
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

}