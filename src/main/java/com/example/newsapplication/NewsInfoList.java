package com.example.newsapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class NewsInfoList {
    public List<String> categoryList;
    public List<String> AbandonedcategoryList;
    public List<NewsItem> FavoriteNewsList;
    public List<NewsItem> HistoryNewsList;
    private Context context;


    public NewsInfoList(Context context)
    {
        this.context = context;
        categoryList = new ArrayList<String>();
        FavoriteNewsList = new ArrayList<NewsItem>();
        HistoryNewsList = new ArrayList<NewsItem>();
//        String[] titles = new String[]{"娱乐","军事","教育","文化","健康","财经","体育","汽车","科技","社会"};
//        categoryList.addAll(Arrays.asList(titles));
        AbandonedcategoryList = new ArrayList<String>();
    }

    private void setFavoriteListToDB(NewsItem item, SQLiteDatabase db) {
        System.out.println("setFavoriteListToDB" + item.title);
        if (item.favorite) {
            Cursor cursor = db.rawQuery("select * from FavoriteListTable where title = ? and timestamp = ?",
                    new String[]{item.title, item.timestamp});
            if (cursor.getCount() == 0) {
                Log.d("TAG", "setFavoriteListToDB: we get a favorite not in list");
                db.execSQL("insert into FavoriteListTable(title, imageURL,videoURL,content,publisher,timestamp,read,favorite) values(?,?,?,?,?,?,?,?)",
                        new Object[]{item.title, item.imageURL,item.videoURL, item.content, item.publisher, item.timestamp,1,1});
            } else {
                Log.d("TAG", "setFavoriteListToDB: repeated");
            }
        }
        else{ db.execSQL("delete from FavoriteListTable where title = ? and timestamp = ?",
                new Object[]{item.title, item.timestamp});}
        db.rawQuery("select * from FavoriteListTable order by timestamp",null);
    }

    public void setFavorite(NewsItem item){
        System.out.println("setFavorite"+item.title);
        SQLiteDatabase db = ((NewsApplication)context).databaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from FavoriteListTable where title = ? and timestamp = ?",
                new String[]{item.title, item.timestamp});

        if(cursor.getCount() == 0){
            item.favorite = true;
            if(!FavoriteNewsList.contains(item)) FavoriteNewsList.add(item);
        }
        else{
            item.favorite = false;
            if(FavoriteNewsList.contains(item)) FavoriteNewsList.remove(item);
        }
        setFavoriteListToDB(item, db);
    }

    public void setHistory(NewsItem item) {
        Date date = new Date();
        System.out.println("setHistory" + item.title);
        SQLiteDatabase db = ((NewsApplication) context).databaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from HistoryListTable where title = ? and timestamp = ?",
                new String[]{item.title, item.timestamp});
        if (cursor.getCount() == 0) {
            Log.d("TAG", "setHistory: First Time to read: " + item.title);
            item.read = true;
            int i;if(item.favorite) i=1;else i=0;
            db.execSQL("insert into HistoryListTable(title, imageURL,videoURL,content,publisher,timestamp,favorite,read,addtimestamp) values(?,?,?,?,?,?,?,?,?)",
                    new Object[]{item.title, item.imageURL, item.videoURL,item.content, item.publisher, item.timestamp, tBooltoInt(item.favorite), 1,date.getTime()});

            if (HistoryNewsList.contains(item)) {
                Log.d("TAG", "setHistory: List contains the first time to read");
            }
            HistoryNewsList.add(item);
            Log.d("TAG", "setHistory: added in historynewslist: len= "+ HistoryNewsList.size());
        }
        else{
            db.execSQL("update HistoryListTable set addtimestamp = ? where title = ? and timestamp = ?",
                new Object[]{date.getTime(),item.title, item.timestamp});
        }
        db.rawQuery("select * from HistoryListTable order by addtimestamp",null);
    }

    public  int tBooltoInt(boolean b)
    {
        if(b) return 1;else return 0;
    }
}
