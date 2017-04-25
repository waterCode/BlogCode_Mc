package com.example.download;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    private static final String testUrl="http://gdown.baidu.com/data/wisegame/2a0117626c1a273f/mojitianqi_6030202.apk";
    private DownloadService.DownloadManager downloadService;
    private FileInfo fileInfo;
    private ServiceConnection serviceConnection;
    private TextView tv_fileName;
    private SeekBar seekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter=new IntentFilter(DownloadService.ACTION_UPDATE);
        registerReceiver(receiver,intentFilter);
    }

    private void initView() {
         tv_fileName = (TextView) findViewById(R.id.fileName);
        seekBar= (SeekBar) findViewById(R.id.progress_seekBar);

    }

    private void initData() {
        fileInfo=new FileInfo();
        fileInfo.setUrl(testUrl);
        fileInfo.setFileName("mojitianqi_6030202.apk");
        Intent intent=new Intent(this,DownloadService.class);
        intent.putExtra("fileinfo",fileInfo);
        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                downloadService= (DownloadService.DownloadManager) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);//绑定服务




    }



    public void onclick(View v){
        switch (v.getId()){
            case R.id.begin_download:downloadService.beginDownload(fileInfo);break;
            case R.id.stop:downloadService.stopDownload(0);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                long progress = intent.getLongExtra("downloadProgress", 0);
                Log.d("MainActivity",""+progress+"%");
                seekBar.setProgress((int) progress);
            }
        }
    };
}
