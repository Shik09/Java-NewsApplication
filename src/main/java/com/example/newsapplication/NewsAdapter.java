package com.example.newsapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsMenuViewHolder> {
    List<NewsItem> mList;
    Context context;

    public NewsAdapter(List<NewsItem> newsList,Context con) {
        this.mList = newsList;
        context = con;
    }

    static class NewsMenuViewHolder extends RecyclerView.ViewHolder{
        public TextView news_title; //标题
        public TextView news_time;//内容
//        public ImageView news_image; //图片

        public NewsMenuViewHolder(@NonNull View itemView)
        {
            super(itemView);
//            news_image = itemView.findViewById(R.id.iv_image);
            news_title = itemView.findViewById(R.id.tv_title);
            news_time = itemView.findViewById(R.id.tv_time);
        }
    }

    @Override
    public int getItemCount(){
        if(mList == null) {
            return 0;
        }
        Log.d("TAG", "NewsAdapter: normal:"+mList.size());
        return this.mList.size();
    }

    @NonNull
    @Override
    public NewsMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsMenuViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsMenuViewHolder holder, int position) {
        NewsItem c = mList.get(position);
        holder.news_title.setText(c.title);
        holder.news_time.setText(c.timestamp);

        if(((NewsApplication)context.getApplicationContext()).check_in(c))
        {
            holder.news_title.setTextColor(Color.GRAY);
            holder.news_time.setTextColor(Color.GRAY);
            c.read = true;
        }

//        if(c.imageURL == null){
//            Log.d("TAG", "onBindViewHolder:imageURL null ");}
//        else if(c.imageURL.size()!=0) {
//            ImageLoader imloader = new ImageLoader();
//            imloader.showImageByThread(holder.news_image, c.imageURL.get(0));
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //监听事件提供跳转新闻详情
            @Override
            public void onClick(View view) {
//                ((NewsApplication)context.getApplicationContext()).newsCategoryList.setHistory(c);

                Intent intent = new Intent(context,DetailActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putSerializable("NewsItem",c);
                intent.putExtra("message",myBundle);
                context.startActivity(intent);
            }
        });
    }
}
