package com.example.pokeremotionapplication.ui.chat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pokeremotionapplication.R;
import com.example.pokeremotionapplication.data.ChatDataBaseHelper;

public class ChatActivity extends AppCompatActivity {

    private ChatDataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
    }

    // 初始化
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


        // 返回键
        ImageView back = findViewById(R.id.chat_back_button);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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

}