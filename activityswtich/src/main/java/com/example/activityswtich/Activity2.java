package com.example.activityswtich;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by zmc on 2017/3/29.
 */
public class Activity2 extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_main);
        TextView textView= (TextView) findViewById(R.id.tv);
        textView.setText("hhhhh");
    }
}
