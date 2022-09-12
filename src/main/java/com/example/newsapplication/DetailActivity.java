package com.example.newsapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileInputStream;

public class DetailActivity extends AppCompatActivity {
    private NewsItem news;
    private Toolbar head;


    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(menuItem.getItemId() == R.id.action_detail_star) {
                ((NewsApplication)getApplication()).newsCategoryList.setFavorite(news);
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        Bundle myBundle = intent.getBundleExtra("message");
        news = (NewsItem) myBundle.getSerializable("NewsItem");
        if(news.videoURL.equals(""))
        {
            setContentView(R.layout.activity_datails_without_video);
            Log.d("TAG", "DETAIL onCreate: WITHOUT VIDEO");
        }
        else setContentView(R.layout.activity_details);

        head = (Toolbar)findViewById(R.id.detail_toolbar);
        head.inflateMenu(R.menu.menu_detail_page_toolbar);
        head.setOnMenuItemClickListener(onMenuItemClick);

        TextView title = (TextView)findViewById(R.id.de_title);
        title.setText(news.title);
        TextView res = (TextView)findViewById(R.id.de_res);
        res.setText(news.publisher);
        TextView time= (TextView)findViewById(R.id.de_time);
        time.setText(news.timestamp);

        ImageView image = (ImageView)findViewById(R.id.de_image);
        if (news.imageURL != null && !news.imageURL.equals("")) {
            if(((NewsApplication)getApplication()).check_in(news)){
                String StoragePath = this.getFilesDir() + "/Newsimages/" + news.imageURL.substring(11,16);
                Bitmap bit = getBitmapFromPath(StoragePath);
                image.setImageBitmap(bit);
            }
            else{
                ImageLoader imloader = new ImageLoader(this);
                imloader.showImageByThread(image, news.imageURL);
            }
        }

        if (news.videoURL != null && !news.videoURL.equals("")) {
            VideoView video = (VideoView)findViewById(R.id.de_video);
            video.setVideoURI(Uri.parse(news.videoURL));
            MediaController mediaController=new MediaController(this);
            mediaController.setMediaPlayer(video);
            video.setMediaController(mediaController);
            video.requestFocus();
        }


        TextView content = (TextView)findViewById(R.id.de_content);
        content.setText(news.content);
        content.setMovementMethod(ScrollingMovementMethod.getInstance());

        ((NewsApplication)getApplication()).newsCategoryList.setHistory(news);

    }

    public static Bitmap getBitmapFromPath(String path){
        if (!new File(path).exists()) {
            Log.d("TAG", "getBitmapFromPath: PATH NOT EXIST");
            return null;
        }
        //最大读取10M的图片
        byte[] buf = new byte[1024*1024*10];
        Bitmap bitmap = null;

        try {
            FileInputStream fis = new FileInputStream(path);
            int len = fis.read(buf, 0, buf.length);
            bitmap = BitmapFactory.decodeByteArray(buf, 0, len);
            if (bitmap == null) {
                return null;
            }
            fis.close();
        } catch (Exception e) {
            return null;
        }
        return bitmap;
    }
}
