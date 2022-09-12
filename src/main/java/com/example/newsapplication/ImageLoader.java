package com.example.newsapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader {
    private ImageView mImageView;
    private Context conte;
    private String mUrl;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mImageView.setImageBitmap((Bitmap) msg.obj);
        }
    };


    public ImageLoader(Context context)
    {
        conte = context;
    }
    /**
     * 通过多线程 下载图片
     *
     * @param imageView
     * @param url
     */
    public void showImageByThread(final ImageView imageView, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mImageView = imageView;
                Bitmap bitmap = getBitmapFromUrl(url);
                saveBitmap(url.substring(11,16),bitmap,conte);
                Message message = Message.obtain();
                message.obj = bitmap;
                handler.sendMessage(message);
            }
        }).start();
    }


    /**
     * 下载图片
     *
     * @param urlstring 图片地址
     */
    public Bitmap getBitmapFromUrl(String urlstring) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            Log.d("TAG", "getBitmapFromUrl: ready to get bitmap from " + urlstring);
            URL url = new URL(urlstring);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                is = new BufferedInputStream(con.getInputStream());
                bitmap = BitmapFactory.decodeStream(is);
                con.disconnect();
                Log.d("TAG", "getBitmapFromUrl: return bitmap");
                return bitmap;
            } else {
                Log.d("TAG", "getBitmapFromUrl: Error responsecode" + responseCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("TAG", "getBitmapFromUrl: return null");
        return null;
    }

    /**
     * @param imageView
     * @param mUrl
     */
    public void showImageByAsyncTask(ImageView imageView, String mUrl) {
        new ImageAsyncTask(imageView).execute(mUrl);
    }

    /**
     * 异步加载图片
     */
    private class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView mImageView;

        public ImageAsyncTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return getBitmapFromUrl(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mImageView.setImageBitmap(bitmap);
        }
    }

    static void saveBitmap(String name, Bitmap bm, Context mContext) {
        Log.d("Save Bitmap", "Ready to save picture");
        //指定我们想要存储文件的地址
        String TargetPath = mContext.getFilesDir() + "/Newsimages/";
        Log.d("Save Bitmap", "Save Path=" + TargetPath);
        //判断指定文件夹的路径是否存在
        if (!fileIsExist(TargetPath)) {
            Log.d("Save Bitmap", "TargetPath isn't exist");
        } else {
            File saveFile = new File(TargetPath, name);

            try {
                FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                // compress压缩
                bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut);
                //存储完成后需要清除相关的进程
                saveImgOut.flush();
                saveImgOut.close();
                Log.d("Save Bitmap", "The picture is save to your phone!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    static boolean fileIsExist(String fileName)
    {
        //传入指定的路径，然后判断路径是否存在
        File file=new File(fileName);
        if (file.exists())
            return true;
        else{
            //file.mkdirs() 创建文件夹的意思
            return file.mkdirs();
        }
    }
}
