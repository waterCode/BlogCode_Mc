package com.example.alarm_mc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


/**
 * Created by mc on 16-4-27.
 */
public class DeleteAlarmActivity extends AppCompatActivity {

    ListView listView;
    MyListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_delete_view);
        ImageButton button = (ImageButton) findViewById(R.id.calcel_delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView = (ListView)findViewById(R.id.delete_alarm_list);
        initData();

    }

    private void initData(){
        MyDataBaseHelper helper = MyDataBaseHelper.getInstance(this);
        SQLiteDatabase dWriter = helper.getWritableDatabase();
        Cursor cursor = dWriter.query(MyDataBaseHelper.ALARM_TB_NAME,null,null,null,null,null,null);
        String[] alarmColums = new String[]{MyDataBaseHelper.COL_TIME,MyDataBaseHelper.COL_ALARM_REPEAT_TIMES};
        int[] layoutId = new int[]{R.id.alarm_delete_time,R.id.alarm_name_delete};
        adapter = new MyListViewAdapter(this,R.layout.alarm_delete_item,cursor,alarmColums,layoutId, CursorAdapter.FLAG_AUTO_REQUERY);
        listView.setAdapter(adapter);

    }


    public class MyListViewAdapter extends SimpleCursorAdapter{
        public MyListViewAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public void bindView(View view, final Context context, final Cursor cursor) {
            super.bindView(view, context, cursor);
            final int id = cursor.getInt(0);
            ImageButton imageButton = (ImageButton)view.findViewById(R.id.alarm_delete_button);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataBaseOperator operator = new DataBaseOperator(context);
                    LampSharePreference pre = LampSharePreference.getInstance(context);
                    int nums= pre.getInt(LampSharePreference.ALARM_NUMBERS,0);
                    nums--;
                    pre.setInt(LampSharePreference.ALARM_NUMBERS,0);
                    operator.delete(id);
                    cursor.requery();
                    adapter.notifyDataSetChanged();

                }
            });

        }


    }
}
