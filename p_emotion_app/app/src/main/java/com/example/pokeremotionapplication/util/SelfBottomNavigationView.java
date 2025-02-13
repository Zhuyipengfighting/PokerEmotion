package com.example.pokeremotionapplication.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SelfBottomNavigationView extends BottomNavigationView {

    public SelfBottomNavigationView(Context context) {
        super(context);
        init();
    }

    public SelfBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelfBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 设置自定义属性
        Menu menu = getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setIcon(null); // 移除图标
        }
        
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
            int h = child.getMeasuredHeight() + child.getBottom() - child.getTop();
            if (h > height) {
                height = h;
            }
        }
        setMeasuredDimension(width, height);
    }
}

