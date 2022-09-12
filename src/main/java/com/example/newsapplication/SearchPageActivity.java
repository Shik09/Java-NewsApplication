package com.example.newsapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SearchPageActivity extends AppCompatActivity {
    Button search_button;
    Fragment fra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);
        search_button = (Button)findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText ec = (EditText)findViewById(R.id.se_category);
                EditText ek = (EditText)findViewById(R.id.se_keyword);
                EditText es = (EditText)findViewById(R.id.se_starttime);
                EditText ee = (EditText)findViewById(R.id.se_endtime);
                String cc = ec.getText().toString();
                String ck = ek.getText().toString();
                String cs = es.getText().toString();
                String ce = ee.getText().toString();
                Log.d("TAG", "SearchPageActivityonClick: "+ cc + ck + cs + ce);
                Intent intent = new Intent(SearchPageActivity.this,SearchResultActivity.class);
                Bundle myBundle = new Bundle();
                ArrayList<String> li = new ArrayList<String>();
                li.add(ck);li.add(cc);li.add(cs);li.add(ce);
                myBundle.putStringArrayList("StringParams",li);
                intent.putExtra("message",myBundle);
                SearchPageActivity.this.startActivity(intent);
            }
        });
    }
}
