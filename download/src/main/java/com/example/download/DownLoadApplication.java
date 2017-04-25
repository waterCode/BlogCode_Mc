package com.example.download;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by zmc on 2017/4/16.
 */
public class DownloadApplication extends Application {

    public static DownloadApplication instance;
    private DaoMaster.DevOpenHelper helper;
    private DaoMaster daoMaster;
    private SQLiteDatabase db;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        helper=new DaoMaster.DevOpenHelper(this,"download.db",null);

        db=helper.getWritableDatabase();
        daoMaster=new DaoMaster(db);
        daoSession=daoMaster.newSession();

    }


    public SQLiteDatabase getDb() {
        return db;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static DownloadApplication getInstance(){
        return instance;
    }
}
