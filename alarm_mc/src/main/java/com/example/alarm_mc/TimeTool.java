package com.example.alarm_mc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mc on 2016/4/6.
 */
public class TimeTool {


    public static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        return format.format(now);
    }


    public static Date turnStringToDate(String sTime){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date now = null;
        try {
            now =format.parse(sTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return now;
    }

    public static String turnDateToString(Date date){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time;
        time =format.format(date);
        return time;

    }

    public static String turnDateToStringonlyTime(Date date){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String time;
        time =format.format(date);
        return time;
    }

    public static long getDiffTime(Date offTime_date, Date nowTime_date) {
        long offTime=offTime_date.getTime();
        long nowTime=nowTime_date.getTime();
        long diffTime=nowTime-offTime;
        return diffTime;

    }


    public static String turnMiuTimeToString(long time){
        long hour=time/(1000*60*60);
        long min = (time-hour*(1000*60*60))/(1000*60);
        return hour+":"+min;
    }

    public static long turnTimeToMin(long time){
        return time/(1000*60);
    }



}
