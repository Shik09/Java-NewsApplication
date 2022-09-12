package com.example.newsapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory cursorFactory, int version){
        super(context, name, cursorFactory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table if not exists FavoriteListTable " +
                "(title TEXT," +
                "imageURL TEXT,"+
                "videoURL TEXT,"+
                "content TEXT," +
                "publisher TEXT," +
                "timestamp VARCHAR(20)," +
                "read INTEGER," +
                "favorite INTEGER)";
        db.execSQL(createTable);

        createTable = "create table if not exists HistoryListTable " +
                "(title TEXT," +
                "imageURL TEXT,"+
                "videoURL TEXT,"+
                "content TEXT," +
                "publisher TEXT," +
                "timestamp VARCHAR(20)," +
                "read INTEGER," +
                "favorite INTEGER," +
                "addtimestamp INTEGER)";
        db.execSQL(createTable);

        createTable = "create table if not exists SelectedCategoryTable (name TEXT)";
        db.execSQL(createTable);
        createTable = "create table if not exists AbandonedCategoryTable (name TEXT)";
        db.execSQL(createTable);
        Cursor c = db.rawQuery("select * from SelectedCategoryTable", null);
        int number=c.getCount();
        c.close();
        if(number == 0){
            db.execSQL("insert into SelectedCategoryTable(name) values ('娱乐')");
            db.execSQL("insert into SelectedCategoryTable(name) values ('军事')");
            db.execSQL("insert into SelectedCategoryTable(name) values ('教育')");
            db.execSQL("insert into SelectedCategoryTable(name) values ('文化')");
            db.execSQL("insert into SelectedCategoryTable(name) values ('健康')");
            db.execSQL("insert into SelectedCategoryTable(name) values ('财经')");
            db.execSQL("insert into SelectedCategoryTable(name) values ('体育')");
            db.execSQL("insert into SelectedCategoryTable(name) values ('汽车')");
            db.execSQL("insert into SelectedCategoryTable(name) values ('科技')");
            db.execSQL("insert into SelectedCategoryTable(name) values ('社会')");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("update SQLite database");
    }
}

