package com.example.pokeremotionapplication.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Insets;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pokeremotionapplication.MainActivity;
import com.example.pokeremotionapplication.R;
import com.example.pokeremotionapplication.ui.home.ui_fragment.HomeAddFragment;
import com.example.pokeremotionapplication.ui.home.ui_fragment.HomeSettingFragment;
import com.example.pokeremotionapplication.ui.home.ui_fragment.HomeStateFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    List<Fragment> fragmentList;

    // 颜色参数
    private boolean isStateSelected = false;
    private boolean isSettingSelected = false;
    private boolean isAddSelected = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 在 Fragment 的 onCreateView 方法中，getView() 可能返回 null，
        // 因为视图尚未完全创建。因此，建议将 setRouter() 方法移到 onViewCreated 中，以确保视图已经初始化完成
        setRouter();

        setDropDownMenu(view);
    }

    // show fragment
    public void showFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        // 替换Fragment
        fragmentTransaction.replace(R.id.home_fragment,fragment);
        // 提交
        fragmentTransaction.commit();
    }


    // 实现home内部页面的切换
    @SuppressLint("ResourceAsColor")
    private void setRouter() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeStateFragment());
        fragmentList.add(new HomeSettingFragment());
        fragmentList.add(new HomeAddFragment());
        //默认显示第一个状态fragment
        showFragment(fragmentList.get(0));

        TextView setting = requireView().findViewById(R.id.home_top_menu_setting);
        TextView state = requireView().findViewById(R.id.home_top_menu_state);
        ImageView add = requireView().findViewById(R.id.home_top_menu_add);

        // 最初的颜色
        isStateSelected = true;
        isSettingSelected = false;
        isAddSelected = false;
        state.setSelected(true);
        setting.setSelected(false);

        setting.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (!isSettingSelected) {
                    showFragment(fragmentList.get(1));
                    isSettingSelected = true;
                    isStateSelected = false;
                    isAddSelected = false;
                    setting.setSelected(true);
                    state.setSelected(false);
                    add.setSelected(false);
                    add.setImageResource(R.drawable.add);
                }
            }
        });

        state.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (!isStateSelected) {
                    showFragment(fragmentList.get(0));
                    isStateSelected = true;
                    isSettingSelected = false;
                    isAddSelected = false;
                    state.setSelected(true);
                    setting.setSelected(false);
                    add.setSelected(false);
                    add.setImageResource(R.drawable.add);
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (!isAddSelected) {
                    showFragment(fragmentList.get(2));
                    isStateSelected = false;
                    isSettingSelected = false;
                    isAddSelected = true;
                    state.setSelected(false);
                    setting.setSelected(false);
                    add.setSelected(true);
                    add.setImageResource(R.drawable.add_2);
                }
            }
        });

    }

    // 实现左侧侧拉菜单
    public void setDropDownMenu(View view){

        // 绑定
        DrawerLayout drawerLayout = view.findViewById(R.id.home_drawer_layout);
        NavigationView navigationView = view.findViewById(R.id.home_navigation);
        ImageView imageView_button = view.findViewById(R.id.home_top_menu_menu);

        imageView_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }

}