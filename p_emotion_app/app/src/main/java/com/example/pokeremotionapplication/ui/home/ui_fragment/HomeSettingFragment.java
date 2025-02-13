package com.example.pokeremotionapplication.ui.home.ui_fragment;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.pokeremotionapplication.R;

public class HomeSettingFragment extends Fragment {

    boolean isSwitch = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home_seting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getCircleImg(view);

        setSetting(view);
    }

    // home_setting的设置按钮
    public void setSetting(View view){
        ImageView switchButton = view.findViewById(R.id.home_setting_switch);

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSwitch){
                    isSwitch = false;
                    switchButton.setImageResource(R.drawable.switch_close);
                }else {
                    isSwitch = true;
                    switchButton.setImageResource(R.drawable.switch_open);
                }
            }
        });
    }

    // home_state页面中的脑机接口图片
    public void getCircleImg(View view){
        // 绑定图片位置
        ImageView imageView = view.findViewById(R.id.circular_image_view);

        // 获取圆形 shadow
        Drawable shadowDrawable = AppCompatResources.getDrawable(requireContext(), R.drawable.shadow);

        // 加载图片并应用圆形变换
        Glide.with(this)
                .load(R.drawable.facility) // 替换为你的图片资源
                .apply(RequestOptions.bitmapTransform(new CircleCrop())) // 应用圆形变换
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        // 创建一个新的 LayerDrawable，包含圆形边框和图片
                        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                                shadowDrawable,
                                resource
                        });
                        imageView.setImageDrawable(layerDrawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // 清除资源
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        // 加载失败
                    }
                });

    }

}