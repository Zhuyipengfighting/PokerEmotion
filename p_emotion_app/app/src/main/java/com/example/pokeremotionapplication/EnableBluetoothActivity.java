package com.example.pokeremotionapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

public class EnableBluetoothActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            finish(); // 如果蓝牙已启用或设备不支持蓝牙，直接关闭 Activity
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙已启用", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "蓝牙未启用，部分功能可能无法使用", Toast.LENGTH_SHORT).show();
            }
        }
        finish(); // 关闭 Activity
    }
}