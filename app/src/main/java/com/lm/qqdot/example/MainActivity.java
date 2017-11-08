package com.lm.qqdot.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.list);
        String[] data = new String[30];
        for (int i = 0; i < data.length; i++) {
            data[i] = "test" + i;
        }
        listView.setAdapter(new QQDotArrayAdapter(this,R.layout.item,R.id.text,data));
    }
}
