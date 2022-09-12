package com.example.newsapplication;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

public class NewsItem implements Serializable{
    public String title;
    public String imageURL;
    public String videoURL;
    public String content;
    public String publisher;
    public String timestamp;
    public boolean read;
    public boolean favorite;
//    public NewsCategoryList.NewsCategory category;

    public NewsItem()
    {
        read = false;
        favorite = false;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(!(obj instanceof NewsItem)) return false;
        NewsItem newsObj = (NewsItem)obj;
        if(!this.title.equals(newsObj.title)) return false;
        if(this.timestamp.equals(newsObj.timestamp)) return false;
        return true;
    }
}
