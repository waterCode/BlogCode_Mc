package com.example.bluetoothconnection;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by zmc on 9/3/2016.
 */
public class DeviceListActivity extends Activity {

    private static final String TAG ="DeviceListActivity" ;
    public static final String EXTRA_DEVICE_ADDRESS ="device_address";
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private BluetoothAdapter mBtAdapter;
    //广播接受器
    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 拿到action，并且判断是找到蓝牙，还是蓝牙搜索完毕
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果找到的是已经匹配，跳过显示，上面已经显示
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // 完成搜索，还原title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        // 如果点击activity外部分就退出，设置result为cancel
        setResult(Activity.RESULT_CANCELED);

        // 初始化搜索按钮
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });


        // 初始化适配器，一个已经匹配的适配器，一个是显示新设备的适配器，分别对应两个listview
        ArrayAdapter<String> pairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        
        

        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // 设置listviewadapter和监听
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        this.registerReceiver(mReceiver,filter);

        filter=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver,filter);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();

        if(bondedDevices.size()>0){
            for(BluetoothDevice device:bondedDevices){
                pairedDevicesArrayAdapter.add(device.getName()+"\n"+device.getAddress());
            }
        }else {
            String deviceName=getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(deviceName);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");
        setTitle(R.string.scanning);

        // 搜索是显示title
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // 如果已经在搜索，则停下再开始
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        mBtAdapter.startDiscovery();
    }
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // 取消搜索，因为要开始连接，否则会连接很慢
            mBtAdapter.cancelDiscovery();

            // 拿到地址，设置到Activity的result里，结束时返回去
            String info = ((TextView) v).getText().toString();
            Log.d(TAG,info);
            if(!info.equals("没有已匹配设备")){
                String address = info.substring(info.length() - 17);

                // 设置intent，把地址写进去
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

                // 设置result
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    };



}
