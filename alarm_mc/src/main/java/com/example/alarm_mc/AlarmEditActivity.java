package com.example.alarm_mc;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zdd on 16-7-23.
 */
public class AlarmEditActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener {


    private String TAG = "AlarmEditActivity";
    private ListView listView;
    private List<Map<String, String>> datalist;
    private Map<String, String> map;
    private SimpleAdapter simpleAdapter;
    private Alarm alarm;
    DataBaseOperator dbOperator;

    Calendar time_calender;
    String modify_time_string;


    LampSharePreference alarmPre;//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_edit);

        alarmPre= LampSharePreference.getInstance(this);
        dbOperator = new DataBaseOperator(this);
        modify_time_string=getIntent().getStringExtra("time");

        alarm=new Alarm();

        if(modify_time_string!=null){
            Log.d(TAG,"modify");
            alarm.setAlarmChangeWay("modify");
            alarm.setTime(modify_time_string);
            alarm.setWhichAlarm(getIntent().getIntExtra("position",0));
            Log.d(TAG,"修改位置为"+alarm.getWhichAlarm());
        }else{
            alarm.setAlarmChangeWay("new");
            alarm.setWhichAlarm(alarmPre.getInt(LampSharePreference.ALARM_NUMBERS,0)+1);
            Log.d(TAG,"new");
        }
        datalist = new ArrayList<>();


        ImageButton saveAlarm = (ImageButton) findViewById(R.id.save_alarm);
        saveAlarm.setOnClickListener(this);

        ImageButton cacelEditAlarm = (ImageButton) findViewById(R.id.cancel_edit_alarm);
        cacelEditAlarm.setOnClickListener(this);

        Date date = TimeTool.turnStringToDate(alarm.getAlarmTime()+":00");

        time_calender=Calendar.getInstance();
        Log.d("现在时间为",""+time_calender.getTime());
        time_calender.set(Calendar.HOUR_OF_DAY,date.getHours());//闹钟时间，新建的话就是当前时间
        time_calender.set(Calendar.MINUTE,date.getMinutes());
        Log.d("time_calendar为",""+time_calender.getTime());

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onstop");
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(time_calender.getTime().getHours());
        timePicker.setCurrentMinute(time_calender.getTime().getMinutes());
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minuite) {

                time_calender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                time_calender.set(Calendar.MINUTE, minuite);
                time_calender.set(Calendar.SECOND, 0);
                if (time_calender.before(Calendar.getInstance())) {
                    time_calender.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
        });


        listView = (ListView) findViewById(R.id.alarm_edit_list_view);
        setDataList();
        simpleAdapter = new SimpleAdapter(this, datalist, R.layout.alarm_edit_item,
                new String[]{"name", "value"}, new int[]{R.id.name, R.id.value});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(this);
    }


    public void setDataList() {
        datalist.clear();
        map = new HashMap<>();
        map.put("name", "重复次数");
        map.put("value", alarm.getRepeatTimes());
        datalist.add(map);
        map = new HashMap<>();
        map.put("name", "目标");
        map.put("value", alarm.getAlarmStatus());
        datalist.add(map);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.save_alarm:
                saveAlarm();
                finish();
                break;
            case R.id.cancel_edit_alarm:
                finish();
                break;
        }
    }

    private void saveAlarm() {

        String time_string = TimeTool.turnDateToStringonlyTime(time_calender.getTime());
        Log.d(TAG,time_string);
        Log.d(TAG,alarm.getAlarmStatus());
        Log.d(TAG,alarm.getRepeatTimes());
        if (time_calender.before(Calendar.getInstance())){//如果时间早于现在就是天数+1
            time_calender.set(Calendar.DAY_OF_MONTH,time_calender.get(Calendar.DAY_OF_MONTH)+1);
            Log.d(TAG,"闹钟天数为"+time_calender.get(Calendar.DAY_OF_MONTH));

        }
        ContentValues values = new ContentValues();
        values.put(MyDataBaseHelper.COL_TIME, time_string);
        values.put(MyDataBaseHelper.COL_ALARM_STATUS, alarm.getAlarmStatus());
        values.put(MyDataBaseHelper.COL_ALARM_REPEAT_TIMES,alarm.getRepeatTimes());

        Intent intent=new Intent(this, AlarmReceiver.class);//启动broadcast的intent
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        if(alarm.getAlarmChangeWay().equals("new")){

            dbOperator.intsert(MyDataBaseHelper.ALARM_TB_NAME, values);
            Log.d(TAG,"insert to dataBase");
            Log.d(TAG,"设置闹钟时间为"+time_calender.getTime());
            intent.putExtra("time",time_string);
            //更新闹钟数量
            alarmPre.setInt(LampSharePreference.ALARM_NUMBERS,alarm.getWhichAlarm());


        }else {
            dbOperator.update(MyDataBaseHelper.ALARM_TB_NAME,values,"alarm_time = ?",new String[]{modify_time_string});
            Log.d(TAG,"update to dataBase");
            Log.d(TAG,"操作时间为"+time_calender.getTime());
            intent.putExtra("time",time_string);
        }

        Log.d(TAG,"这是第几个闹钟呢,请回答"+alarm.getWhichAlarm());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,alarm.getWhichAlarm(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP,time_calender.getTimeInMillis(),pendingIntent);




        alarm.setTime(time_string);
        alarm.setCommand("alarm");

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                chooseRepeatTimes();
                break;
            case 1:
                chooseAlarmStatus();
        }
    }

    private void chooseAlarmStatus() {

        String items[] = {"ON","OFF"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("目标")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:alarm.setAlarmStatus("ON");break;
                            case 1:alarm.setAlarmStatus("OFF");break;
                        }

                        setDataList();
                        simpleAdapter.notifyDataSetChanged();
                    }
                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void chooseRepeatTimes() {

        String items[] = {"仅一次","每天"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("重复次数")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            case 0:
                                alarm.setRepeatTimes("仅一次");
                                alarm.setRepeatTimes_int(0);
                                break;
                            case 1:
                                alarm.setRepeatTimes("每天");
                                alarm.setRepeatTimes_int(1);
                                break;
                        }
                        setDataList();
                        simpleAdapter.notifyDataSetChanged();
                    }
                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();

    }

}
