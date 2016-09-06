package com.example.alarm_mc;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zdd on 16-7-23.
 */
public class AlarmShowActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener{



    private ListView listView;//alarm show list
    private ArrayList<String> sList=new ArrayList<>();
    private ImageButton iButton;//add clock button
    private SimpleCursorAdapter cursorAdapter;
    private DataBaseOperator dbOpeater;
    private SQLiteDatabase wb;
    private Cursor mCursor;//数据库指针
    private final String TAG="AlarmShowActivity";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alarm_show_view);
        listView=(ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        iButton=(ImageButton)findViewById(R.id.add_button);
        iButton.setOnClickListener(this);
        dbOpeater = new DataBaseOperator(this);//数据库对象

    }


    @Override
    protected void onResume() {
        super.onResume();
        mCursor=dbOpeater.query(MyDataBaseHelper.ALARM_TB_NAME);//获得alarm的table
        String [] colums = {MyDataBaseHelper.COL_TIME,MyDataBaseHelper.COL_ALARM_STATUS,MyDataBaseHelper.COL_ALARM_REPEAT_TIMES};
        int[] layoutsId = {R.id.alarm_time,R.id.alarm_status,R.id.alarm_repeat_times};
        cursorAdapter=new SimpleCursorAdapter(this,R.layout.alarm_item,mCursor,colums,layoutsId, CursorAdapter.FLAG_AUTO_REQUERY);
        listView.setAdapter(cursorAdapter);
    }

    @Override
    protected void onStop() {
        mCursor.close();
        super.onStop();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.add_button:
                Intent intent =new Intent(this,AlarmEditActivity.class);
                startActivity(intent);
                //cursorAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Intent intent=new Intent(this,AlarmEditActivity.class);
        TextView modify_time= (TextView) view.findViewById(R.id.alarm_time);
        intent.putExtra("time",modify_time.getText());
        intent.putExtra("position",position+1);
        Log.d(TAG,"要修改时间为"+modify_time.getText());
        startActivity(intent);


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,DeleteAlarmActivity.class);
        startActivity(intent);
        return false;
    }
}
