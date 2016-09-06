package com.example.alarm_mc;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zdd on 16-7-25.
 */
public class LampSharePreference {


    public static LampSharePreference lampSharePreference;


     private static final String LAMP_PRE="lamp_pre";
     public static final String LAMP_BT_STATUS="bt_status";
     public static final String LAMP_HUMIDITY="humidity";
     public static final String LAMP_BRIGHTNESS="brightness";
     public static final String LAMP_TEMPERATURE="temperature";
     public static final String LAMP_NOISE="noise";


    public static final String LAMP_SLEEP_MODE="sleep_mode";

    public static final String ALARM_NUMBERS="alarm_numbers";
    private SharedPreferences spre;
    private SharedPreferences.Editor editor;




    private LampSharePreference(Context context) {
        spre=context.getSharedPreferences(LAMP_PRE,Context.MODE_PRIVATE);
        editor=spre.edit();
    }


    public static LampSharePreference getInstance(Context context){
        if (lampSharePreference==null){
            lampSharePreference=new LampSharePreference(context);
        }
        return lampSharePreference;
    }

    public String getString(String key, String defValue) {
        return spre.getString(key, defValue);
    }

    public void setString(String key,String defValue){
        editor.putString(key,defValue);
        editor.commit();
    }

    public void setLong(String key,long defValue){
        editor.putLong(key, defValue);
        editor.commit();
    }
    public void setBoolean(String key,boolean defValue){
        editor.putBoolean(key, defValue);
        editor.commit();
    }
    public void setFloat(String key,float defValue){
        editor.putFloat(key, defValue);
        editor.commit();
    }
    public  void setInt(String key,int defValue){
        editor.putInt(key, defValue);
        editor.commit();
    }
    public  boolean getBoolean(String key,boolean defValue){
        return spre.getBoolean(key,defValue);
    }

    public float getFloat(String key,float defValue){
        return spre.getFloat(key,defValue);
    }

    public int getInt(String key, int defValue) {
        return  spre.getInt(key, defValue);
    }


    public long getLong(String key, long defValue) {
        return spre.getLong(key, defValue);
    }




    void putString(String key,String defValue){
        editor.putString(key,defValue);
        editor.commit();
    }
}
