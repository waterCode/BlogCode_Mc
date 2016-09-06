package com.example.bluetoothconnection;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONNECT_DEVICE=1;//蓝牙连接名字
    private static final int REQUEST_BT_ENABLE=2;

    BluetoothChatService mChatService;

    private final Handler mHandler= new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            switch (msg.what){
                case Constants.MESSAGE_DEVICE_NAME:
                    Bundle bundle=msg.getData();

                    break;
                case Constants.MESSAGE_READ:
                    String jsonData= (String) msg.obj;

            }
        }
    };
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();//拿到蓝牙适配器
        mChatService=new BluetoothChatService(this,mHandler);
    }


    public void onClick(View v){

        if(mBluetoothAdapter.isEnabled()){//要求开启蓝牙
            Intent intent=new Intent(this, DeviceListActivity.class);
            startActivityForResult(intent,REQUEST_CONNECT_DEVICE);
        }else {
            Intent intent =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,REQUEST_BT_ENABLE);

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE:
                if(resultCode== Activity.RESULT_OK){
                    connectDevice(data);
                }
                break;

            case REQUEST_BT_ENABLE:
                if (requestCode==Activity.RESULT_OK){

                }else{
                    Toast.makeText(this, "蓝牙没有开启", Toast.LENGTH_LONG).show();
                }
        }
    }


    private void connectDevice(Intent data) {

        String adress=data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(adress);
        mChatService.connect(device,true);
    }
}
