package com.example.newsapplication;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class NewsApplication extends Application {
    public static DatabaseHelper databaseHelper;
    public NewsInfoList newsCategoryList;



    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(getApplicationContext(), "NewsDatabase", null, 1);
        newsCategoryList = new NewsInfoList(getApplicationContext());
        newsCategoryList.HistoryNewsList = getAll();
        newsCategoryList.FavoriteNewsList = getAllStar();

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor1 = db.rawQuery("select * from SelectedCategoryTable",null);
        if(cursor1.getCount() != 0){
            newsCategoryList.categoryList = new ArrayList<String>();
            while (cursor1.moveToNext()){
                int column_num = cursor1.getColumnIndex("name");
                newsCategoryList.categoryList.add(cursor1.getString(column_num));
            }
        }

        Cursor cursor2 = db.rawQuery("select * from AbandonedCategoryTable",null);
        if(cursor2.getCount() != 0){
            newsCategoryList.AbandonedcategoryList= new ArrayList<String>();
            while (cursor2.moveToNext()){
                int column_num = cursor2.getColumnIndex("name");
                newsCategoryList.AbandonedcategoryList.add(cursor2.getString(column_num));
            }
        }
        Log.d("TAG", "onCreate: application created");
    }

    public boolean check_in(NewsItem item)
    {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from HistoryListTable where title = ? and timestamp = ?",
                new String[]{item.title, item.timestamp});
        if(cursor.getCount()==0) return false;
        else return true;
    }

    public void addCategorytoDB(ArrayList<String> remain,ArrayList<String> delete)
    {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("delete from SelectedCategoryTable");
        db.execSQL("delete from AbandonedCategoryTable");
        for(String item:remain) {db.execSQL("insert into SelectedCategoryTable(name) values(?)", new Object[]{item});}
        for(String item:delete) {db.execSQL("insert into AbandonedCategoryTable(name) values(?)", new Object[]{item});}
        newsCategoryList.categoryList = remain;
        newsCategoryList.AbandonedcategoryList = delete;
    }

    public List<NewsItem> getAll()
    {
        String sql = "select * from HistoryListTable order by addtimestamp desc";
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        List<NewsItem> NewsList = new ArrayList<NewsItem>();
        NewsItem point = null;
        Cursor cursor = db.rawQuery(sql,null);
        Log.d("TAG", "getAll: ");
        while (cursor.moveToNext()) {
            point = new NewsItem();
            int column_num;
            column_num = cursor
                    .getColumnIndex("title");
            point.title = cursor.getString(column_num);
            column_num = cursor
                    .getColumnIndex("content");
            point.content = cursor.getString(column_num);
            column_num = cursor
                    .getColumnIndex("publisher");
            point.publisher = cursor.getString(column_num);
            column_num = cursor
                    .getColumnIndex("imageURL");
            point.imageURL = cursor.getString(column_num);
            column_num = cursor
                    .getColumnIndex("videoURL");
            point.videoURL = cursor.getString(column_num);
            column_num = cursor
                    .getColumnIndex("timestamp");
            point.timestamp = cursor.getString(column_num);
            column_num = cursor
                    .getColumnIndex("read");
            int read = cursor.getInt(column_num);
            if(read > 0) point.read = true;
            else point.read = false;
            column_num = cursor
                    .getColumnIndex("favorite");
            int fav = cursor.getInt(column_num);
            if(fav > 0) point.favorite = true;
            else point.favorite = false;

            NewsList.add(point);
        }
        cursor.close();
        return NewsList;

    }

    public List<NewsItem> getAllStar()
    {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        List<NewsItem> NewsList = new ArrayList<NewsItem>();
        NewsItem point = null;
        Cursor cursor = db.rawQuery("select * from FavoriteListTable order by timestamp desc",null);
        Log.d("TAG", "getAll: ");
        while (cursor.moveToNext()) {
            point = new NewsItem();
            int column_num;
            column_num = cursor
                    .getColumnIndex("title");
            point.title = cursor.getString(column_num);
            column_num = cursor
                    .getColumnIndex("content");
            point.content = cursor.getString(column_num);
            column_num = cursor
                    .getColumnIndex("publisher");
            point.publisher = cursor.getString(column_num);
            column_num = cursor
                    .getColumnIndex("imageURL");
            column_num = cursor
                    .getColumnIndex("imageURL");
            point.imageURL = cursor.getString(column_num);
            column_num = cursor
                    .getColumnIndex("videoURL");
            point.videoURL = cursor.getString(column_num);
            column_num = cursor
                    .getColumnIndex("timestamp");
            point.timestamp = cursor.getString(column_num);
            column_num = cursor
                    .getColumnIndex("read");
            int read = cursor.getInt(column_num);
            if(read > 0) point.read = true;
            else point.read = false;
            column_num = cursor
                    .getColumnIndex("favorite");
            int fav = cursor.getInt(column_num);
            if(fav > 0) point.favorite = true;
            else point.favorite = false;

            NewsList.add(point);
        }
        cursor.close();
        return NewsList;
    }
}

