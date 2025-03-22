package com.example.pokeremotionapplication.data.service;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pokeremotionapplication.EnableBluetoothActivity;
import com.example.pokeremotionapplication.util.BtStateReceiver;

import java.util.Set;

public class BtMonitorService extends Service {

    private static final int REQUEST_ENABLE_BT = 1;

    // 蓝牙状态监听器
    private BtStateReceiver btStateReceiver;

    // 蓝牙适配器
    private BluetoothAdapter bluetoothAdapter;

    // 目标设备名称和地址
    private static final String TARGET_DEVICE_NAME = "YourDeviceName";
    private static final String TARGET_DEVICE_ADDRESS = "YourDeviceAddress";

    @Override
    public void onCreate() {
        super.onCreate();

        // 检查权限
        if (!checkBluetoothPermissions()) {
            Toast.makeText(this, "缺少蓝牙权限，无法启动服务", Toast.LENGTH_SHORT).show();
            stopSelf(); // 停止服务
            return;
        }else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(this, EnableBluetoothActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        // 初始化蓝牙
        initBluetooth();
    }

    // 初始化蓝牙相关逻辑
    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙,请使用具有蓝牙的设备，需求完整服务", Toast.LENGTH_SHORT).show();
            stopSelf(); // 设备不支持蓝牙，停止服务
            return;
        }

        // 注册蓝牙状态监听器
        btStateReceiver = new BtStateReceiver();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(btStateReceiver, filter);

        // 检查蓝牙状态并执行相应操作
        checkBluetoothAndConnect();
    }

    // 检查是否具有蓝牙权限
    private boolean checkBluetoothPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }

    // 检查蓝牙状态并执行相应操作
    private void checkBluetoothAndConnect() {
        if (bluetoothAdapter == null) {
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            // 蓝牙未打开，请求用户打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(enableBtIntent);
        } else {
            // 蓝牙已打开，检查是否连接到特定设备
            checkAndConnectToTargetDevice();
        }
    }

    // 检查是否连接到特定设备，如果没有则尝试连接
    private void checkAndConnectToTargetDevice() {
        try {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            boolean isTargetDeviceConnected = false;

            for (BluetoothDevice device : pairedDevices) {
                if (device.getName() != null && device.getName().equals(TARGET_DEVICE_NAME)) {
                    isTargetDeviceConnected = true;
                    break;
                }
            }

            if (!isTargetDeviceConnected) {
                // 未连接到特定设备，尝试连接
                BluetoothDevice targetDevice = bluetoothAdapter.getRemoteDevice(TARGET_DEVICE_ADDRESS);
                if (targetDevice != null) {
                    Toast.makeText(this, "尝试连接设备: " + TARGET_DEVICE_NAME, Toast.LENGTH_SHORT).show();

                    // 连接设备

                }
            } else {
                Toast.makeText(this, "已连接到设备: " + TARGET_DEVICE_NAME, Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "缺少蓝牙权限，无法访问设备", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 注销蓝牙状态监听器
        if (btStateReceiver != null) {
            unregisterReceiver(btStateReceiver);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}