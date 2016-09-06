package com.example.alarm_mc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zdd on 16-7-15.
 */
public class MyDataBaseHelper extends SQLiteOpenHelper {

    public static MyDataBaseHelper helper;

    private static final int DB_VERSION = 2;
    public static final String COL_ID="_id";
    public static final String DB_NAME ="wrist.db";
    public static final String ACCOUNT_TB_NAME ="account_tb";
    public static final String COl_ACCOUNTS="account";
    public static final String COL_PASSWORD="password";

    public static final String ALARM_TB_NAME="alarm_tb";
    public static final String COL_TIME="alarm_time";
    public static final String COL_ALARM_STATUS="alarm_status";
    public static final String COL_ALARM_REPEAT_TIMES="alarm_times";


    public static final String LAMP_TB_NAME="lamp_tb";
    public static final String COL_LAMP_DATA_TIME="data_time";
    public static final String COL_LAMP_DATA_TEMPERATUE="data_temperature";
    public static final String COL_LAMP_DATA_HUMIDITY="data_humidity";
    public static final String COL_LAMP_DATA_NOISE="data_noise";


    private static final String CREATE_ACCOUNT_TABLE="CREATE TABLE "+ACCOUNT_TB_NAME
            +" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +COl_ACCOUNTS+" TEXT NOT NULL,"
            +COL_PASSWORD+" TEXT NOT NULL);";

    private static final String CREATE_ALARM_TABLE="CREATE TABLE "+ALARM_TB_NAME
            +"(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            +COL_ALARM_STATUS +" TEXT NOT NULL,"
            +COL_ALARM_REPEAT_TIMES+" Text NOT NULL,"
            +COL_TIME+" TEXT NOT NULL);";

    private static final String CREATE_LAMP_TABLE="CREATE TABLE "+LAMP_TB_NAME
            +"("+COL_ID+" INTEGER PRIMARY KEY,"
            +COL_LAMP_DATA_TIME+" TEXT NOT NULL,"
            +COL_LAMP_DATA_TEMPERATUE+" REAL,"
            +COL_LAMP_DATA_NOISE+" INTEGER,"
            +COL_LAMP_DATA_HUMIDITY+" REAL);";



    public MyDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public static synchronized MyDataBaseHelper getInstance(Context context) {

        if (helper == null) {
            helper = new MyDataBaseHelper(context, DB_NAME, null, DB_VERSION);
        }
        return helper;
    }


    private void createTable(SQLiteDatabase db)
    {
        db.execSQL(CREATE_ACCOUNT_TABLE);
        db.execSQL(CREATE_ALARM_TABLE);
        db.execSQL(CREATE_LAMP_TABLE);


    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}
