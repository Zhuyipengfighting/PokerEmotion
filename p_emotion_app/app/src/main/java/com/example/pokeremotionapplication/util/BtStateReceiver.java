package com.example.pokeremotionapplication.util;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

// 后台持续检测蓝牙连接情况
public class BtStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            // 蓝牙状态
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE , BluetoothAdapter.ERROR);

            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    // 蓝牙已关闭
                    Toast.makeText(context, "蓝牙已关闭，设备连接断开", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.STATE_ON:
                    // 蓝牙已打开
                    Toast.makeText(context, "蓝牙已打开", Toast.LENGTH_SHORT).show();
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
