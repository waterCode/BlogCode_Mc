package com.example.alarm_mc;



import java.util.Calendar;

/**
 * Created by zdd on 16-7-23.
 */
public class Alarm {

    private int whichAlarm=1;
    private String command="time";
    private String time;
    private String alarmStatus="ON";
    private String repeatTimes="仅一次";
    private int repeatTimes_int=0;
    private String alarmChangeWay="new";//modify和new两种值

    public int getWhichAlarm() {
        return whichAlarm;
    }

    public Alarm(Calendar time) {
        this.time = TimeTool.turnDateToString(time.getTime());
    }

    public Alarm(){
            this(Calendar.getInstance());
    }

    public String getAlarmChangeWay() {
        return alarmChangeWay;
    }

    public int getRepeatTimes_int() {
        return repeatTimes_int;
    }

    public String getTime() {
        return time;
    }

    public String getCommand() {
        return command;
    }

    public String getAlarmTime() {
        return time;
    }

    public String getRepeatTimes() {
        return repeatTimes;
    }

    public void setRepeatTimes(String repeatTimes) {
        this.repeatTimes = repeatTimes;
    }

    public String getAlarmStatus() {
        return alarmStatus;
    }


    public void setAlarmChangeWay(String alarmChangeWay) {
        this.alarmChangeWay = alarmChangeWay;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setRepeatTimes_int(int repeatTimes_int) {
        this.repeatTimes_int = repeatTimes_int;
    }

    public void setWhichAlarm(int whichAlarm) {
        this.whichAlarm = whichAlarm;
    }

    public void setAlarmStatus(String alarmStatus) {
        this.alarmStatus = alarmStatus;
    }
}
