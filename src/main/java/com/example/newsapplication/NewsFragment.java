package com.example.newsapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private String category;
    private String kwords;
    private String start;
    private String end;
    private List<NewsItem> list;
    private final boolean local;

    public NewsFragment(List<NewsItem> newslist)
    {
        list = newslist;
        local = true;
    }
    public NewsFragment(String category)
    {
        this.category = category;
        kwords="";start="";end="";
        local = false;
    }

    public NewsFragment(String k,String c,String s,String e)
    {
        category = c;kwords = k;start=s;end=e;
        local = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TAG", "FragmentConstructing Category:"+category);
        View view = (ViewGroup) inflater.inflate(
                    R.layout.fragment_content, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView)view.findViewById(R.id.news_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(NewsFragment.this.getContext(), RecyclerView.VERTICAL, false));

        final LinearLayoutManager linearLayoutManager =
                (LinearLayoutManager) recyclerView.getLayoutManager();
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                assert linearLayoutManager != null;
//                int totalCount = linearLayoutManager.getItemCount();
//                int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
//                if(getActivity()!=null && totalCount<=lastVisiblePosition+1){
//                    loadNumber+=10;
//                    loading = true;
//                    getLoaderManager().restartLoader(loaderID, null, thisFragment);
//                }
//            }
//        });


        if(!local) list = new ArrayList<>();
        Log.d("TAG", "listLen: "+list.size());
        NewsAdapter oriadapter = new NewsAdapter(list,getContext());
        recyclerView.setAdapter(oriadapter);
        if(!local) {new NewsAsyncTask().execute(kwords,category,start,end);}
        return view;
    }

    @Override
    public void onRefresh() {
        if (getView() == null) {
            Log.d("TAG", "onRefresh:getView() == null");
            return;
        }
        if(!local) new NewsAsyncTask().execute(kwords,category,start,end);
        else {
            NewsAdapter adapter = new NewsAdapter(list,NewsFragment.this.getContext());
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    private String readStream(InputStream is) {
        InputStreamReader isr;
        String result = "";
        try {
            String line = "";
            isr = new InputStreamReader(is, "UTF-8"); //字节流转化为字符流
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String GetVideoURLfromString(String s)
    {
            Log.d("TAG", "readytoGetVideoURLfromString: "+ s);
            String reg = "https?://\\S+";
            Pattern p = Pattern.compile(reg);
            for(String spl:s.split(","))
            {
                Matcher m = p.matcher(spl);
                if (m.find())
                {
                    Log.d("TAG", "GetURLListfromString:"+ m.group(0) + " is added");
                    String li = m.group(0);
                    return li;
                }
                else {
                    Log.d("TAG", "GetURLListfromString:"+ spl + " not end with certain suffix");
                }
            }
            return "";
    }

    private String GetImageURLfromString(String s)
    {
        if(s.startsWith("[")&&s.endsWith("]")) {
            s = s.substring(1,s.length()-1);
            Log.d("TAG", "readytoGetURLfromString: "+ s);

            String reg = "https?://\\S+";
            Pattern p = Pattern.compile(reg);
            for(String spl:s.split(","))
            {
                Matcher m = p.matcher(spl);
                if (m.find())
                {
                    Log.d("TAG", "GetURLListfromString:"+ m.group(0) + " is added");
                    String li = m.group(0);
                    return li;
                }
                else {
                    Log.d("TAG", "GetURLListfromString:"+ spl + " not end with certain suffix");
                }
            }
            return "";
        }
        else {Log.d("TAG", "GetURLListfromString:"+ s + "not begin and end with []");return "";}
    }

    private List<NewsItem> GetNewsFromAPI(String Keyword, String Category, String StartTime, String EndTime)
    {
        List<NewsItem> newsBeanList = new ArrayList<>();
        JSONObject jsonObject;
        NewsItem newsItem;
        String SearchParams = "&words="+Keyword+"&categories="+Category+"&startDate="+StartTime+"&endDate=";
        if(EndTime.equals("")) {
            Date datenow = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            SearchParams += formatter.format(datenow);}
        else {SearchParams += EndTime;}

        String path = "https://api2.newsminer.net/svc/news/queryNewsList?size=15"+SearchParams;
        Log.d("TAG", "URL: "+path);
        URL url;
        try {
            url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200){
                Log.d("TAG", "GetNewsFromAPI: ");
                InputStream inputStream = connection.getInputStream();
                String json = readStream(inputStream);
                try {
                    jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        newsItem = new NewsItem();
                        String ori_string = jsonObject.getString("image");
                        String ori_video = jsonObject.getString("video");
                        newsItem.imageURL = GetImageURLfromString(ori_string);
                        newsItem.videoURL = GetVideoURLfromString(ori_video);
                        newsItem.title = jsonObject.getString("title");
                        newsItem.content = jsonObject.getString("content");
                        newsItem.timestamp = jsonObject.getString("publishTime");
                        newsItem.publisher = jsonObject.getString("publisher");
                        newsBeanList.add(newsItem);
                        Log.d("TAG","newsBeanList.size() is the "+newsBeanList.size());
                    }
                    return newsBeanList;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(responseCode);
                return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("TAG","Error NULL");
        return null;
    }

    class NewsAsyncTask extends AsyncTask<String,Void,List<NewsItem>> {
        @Override
        protected List<NewsItem> doInBackground(String... strings) {
            list = GetNewsFromAPI(strings[0],strings[1],strings[2],strings[3]);
            return list;
        }

        @Override
        protected void onPostExecute(List<NewsItem> newsItems) {
            super.onPostExecute(newsItems);
            NewsAdapter adapter = new NewsAdapter(list,NewsFragment.this.getContext());
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout.setRefreshing(false);
        }

    }
}
