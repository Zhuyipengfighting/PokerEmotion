package com.example.pokeremotionapplication.util;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pokeremotionapplication.data.service.BtMonitorService;

// 后台持续检测蓝牙连接情况
public class BtStateReceiver extends BroadcastReceiver {

    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            // 蓝牙状态
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE , BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    // 蓝牙已关闭
                    Toast.makeText(context, "蓝牙已关闭，设备连接断开，请打开蓝牙", Toast.LENGTH_SHORT).show();

                    // 申请打开蓝牙
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // 请求权限
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
                        } else {
                            // 已有权限，启动蓝牙启用请求
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(enableBtIntent);
                        }
                    } else {
                        // Android 12 以下版本，直接启动蓝牙启用请求
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(enableBtIntent);
                    }
                    break;
                case BluetoothAdapter.STATE_ON:
                    // 蓝牙已打开
                    Toast.makeText(context, "蓝牙已打开", Toast.LENGTH_SHORT).show();
                    // 通知服务检查并连接设备
                    Intent serviceIntent = new Intent(context, BtMonitorService.class);
                    context.startService(serviceIntent);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    // 蓝牙正在关闭
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    // 蓝牙正在打开
                    break;
            }
        }

    }
}
