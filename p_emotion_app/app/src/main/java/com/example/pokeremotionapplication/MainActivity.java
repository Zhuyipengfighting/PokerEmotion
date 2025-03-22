package com.example.pokeremotionapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.pokeremotionapplication.data.service.BtMonitorService;
import com.example.pokeremotionapplication.data.service.DataReadingService;
import com.example.pokeremotionapplication.ui.chat.ChatFragment;
import com.example.pokeremotionapplication.ui.home.HomeFragment;
import com.example.pokeremotionapplication.ui.my.MyFragment;
import com.example.pokeremotionapplication.util.BtStateReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    // 用于动态请求蓝牙权限
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    // 蓝牙适配器
    private BluetoothAdapter bluetoothAdapter;

    //定义Fragment列表用于切换
    List<Fragment> fragmentList;

    //定义底部导航栏用于切换
    BottomNavigationView bottomNavigationView;

    // 注册蓝牙监听
    private BtStateReceiver btStateReceiver;

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化
        init();

        // 底部导航栏页面切换
        setRouter();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 停止数据读取服务
        Intent serviceIntent = new Intent(this, DataReadingService.class);
        stopService(serviceIntent);

        // 注销蓝牙状态监听器
        if (btStateReceiver != null) {
            unregisterReceiver(btStateReceiver);
        }
    }

    // 初始化
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void init(){
        // 隐藏头
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.hide();

        // 改变顶部状态栏背景
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        changeStatusBarTextColor(window , true);


        // 检查并请求蓝牙权限
        if (checkBluetoothPermissions()) {
            // 权限已授予，启动服务
            startBluetoothServices();

        } else {
            // 请求权限
            requestBluetoothPermissions();
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 检查蓝牙状态
        if (bluetoothAdapter == null) {
            // 设备不支持蓝牙
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
        } else if (!bluetoothAdapter.isEnabled()) {
            // 蓝牙未打开，请求用户打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    // 启动蓝牙相关服务
    private void startBluetoothServices() {
        // 启动数据读取服务
        Intent serviceIntent = new Intent(this, DataReadingService.class);
        startService(serviceIntent);

        // 启动蓝牙监控服务
        Intent btOpenServiceIntent = new Intent(this, BtMonitorService.class);
        startService(btOpenServiceIntent);

    }

    // 更改顶部状态栏的文字颜色
    public void changeStatusBarTextColor(@NonNull Window window, boolean isBlack) {
        View decor = window.getDecorView();
        int flags = 0;
        if (isBlack) {
            //更改文字颜色为深黑色
            flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        else {
            //更改文字颜色为浅色
            flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        }
        decor.setSystemUiVisibility(flags);
    }

    // 显示fragment
    public void showFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // 替换Fragment
        fragmentTransaction.replace(R.id.Fragment,fragment);
        // 提交
        fragmentTransaction.commit();
    }

    // 实现底部导航栏切换页面
    private void setRouter() {
        //初始化fragmentList
        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new ChatFragment());
        fragmentList.add(new MyFragment());
        //默认显示第一个首页fragment
        showFragment(fragmentList.get(0));
        //找到底部导航栏id
        bottomNavigationView = findViewById(R.id.Bottom_menu_view);
        //底部导航栏点击时触发
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_home) {
                    // 显示首页
                    showFragment(fragmentList.get(0));
                    bottomNavigationView.setVisibility(View.VISIBLE);
                } else if (item.getItemId() == R.id.menu_love) {
                    // 显示聊天页面
                    showFragment(fragmentList.get(1));
                    // 让底部导航栏消失
                    bottomNavigationView.setVisibility(View.GONE);

                } else if (item.getItemId() == R.id.menu_my) {
                    // 显示个人中心页面
                    showFragment(fragmentList.get(2));
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

    }

    // 检查是否具有蓝牙权限
    private boolean checkBluetoothPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }

    // 请求蓝牙权限
    private void requestBluetoothPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                },
                REQUEST_BLUETOOTH_PERMISSIONS
        );
    }

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，启动服务
                startBluetoothServices();
            } else {
                // 权限被拒绝，提示用户
                Toast.makeText(this, "蓝牙权限被拒绝，无法使用蓝牙功能", Toast.LENGTH_SHORT).show();
            }
        }
    }


}

