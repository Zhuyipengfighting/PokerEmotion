package com.example.pokeremotionapplication.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AutoSettingLayout extends LinearLayout {

    // 设置项数据
    private List<SettingItem> settingItems = new ArrayList<>();



    public AutoSettingLayout(Context context) {
        super(context);
        // 初始化
        init();
    }

    public AutoSettingLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 初始化
        init();
    }

    public AutoSettingLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化
        init();
    }

    private void init(){
        setOrientation(VERTICAL);
    }

    // 添加多个设置项
    public void addSettingItems(List<SettingItem> items){

        settingItems.addAll(items);
        reFreshLayout();
    }

    // 刷新布局
    private void reFreshLayout(){

        // 清空布局
        removeAllViews();

        for (SettingItem item : settingItems){

        }

    }

    // 设置项数据模型
    public static class SettingItem {

        // 设置项内容描述
        private String title;
        // 设置项图表id
        private int iconResId;

        public SettingItem(String title, int iconResId) {
            this.title = title;
            this.iconResId = iconResId;
        }

        public String getTitle() {
            return title;
        }

        public int getIconResId() {
            return iconResId;
        }
    }
}
