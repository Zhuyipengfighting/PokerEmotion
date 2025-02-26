package com.example.pokeremotionapplication;

import android.app.Application;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class PythonApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化Python环境
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }
}
