package com.example.pokeremotionapplication.ui.home.ui_fragment;

// home页面中的设备状态页面
import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.example.pokeremotionapplication.R;
import com.example.pokeremotionapplication.data.pojo.APreDataPoint;
import com.example.pokeremotionapplication.data.pojo.DataPoint;
import com.example.pokeremotionapplication.util.CSVUtil;
import com.example.pokeremotionapplication.util.DataParser;
import com.example.pokeremotionapplication.util.JsonFileUtils;
import com.example.pokeremotionapplication.util.JsonUtil;
import com.example.pokeremotionapplication.util.ResultStr;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeStateFragment extends Fragment {

    private LineChart lineChart;
    private Thread readingThread;
    private LineData lineData = new LineData();
    private List<String> dataPaths = new ArrayList<>();
    private List<APreDataPoint> newData = new ArrayList<>();
    private Map<String, List<Double>> channels;
    private List<Map<String, List<Double>>> chartData = new ArrayList<>();
    private volatile boolean running = true;
    private final int MAX_DATA_POINTS = 1000; // 展示1000个数据点
    private final int UPDATE_INTERVAL = 500; // 0.5 秒更新一次数据
    private final int UPDATE_DATA_POINTS = 250; // 每次更新250个数据点
    private BroadcastReceiver dataUpdateReceiver;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_state, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getCircleImg(view);

        // 绑定折线图
        lineChart = view.findViewById(R.id.home_state_lineChart);
        lineChart.setDrawBorders(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDragEnabled(false);
        lineChart.setNoDataText("请佩戴设备");

        // 初始化 channels
        channels = new HashMap<>();
        channels.put("Fp1", new ArrayList<>());
        channels.put("Fp2", new ArrayList<>());
        channels.put("F3", new ArrayList<>());
        channels.put("F4", new ArrayList<>());
        channels.put("F7", new ArrayList<>());
        channels.put("F8", new ArrayList<>());
        channels.put("P3", new ArrayList<>());
        channels.put("P4", new ArrayList<>());
        channels.put("O1", new ArrayList<>());
        channels.put("O2", new ArrayList<>());

        // 初始化广播接收器
        dataUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 接收数据
                channels = (Map<String, List<Double>>) intent.getSerializableExtra("channels");
                updateChart();
            }
        };

        // 注册广播接收器
        IntentFilter filter = new IntentFilter("com.example.pokeremotionapplication.DATA_UPDATED");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(dataUpdateReceiver, filter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // 注销广播接收器
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(dataUpdateReceiver);
    }


    // 更新折线图
    private void updateChart() {
        new Handler(Looper.getMainLooper()).post(() -> {
            LineData lineData = new LineData();

            for (Map.Entry<String, List<Double>> entry : channels.entrySet()) {
                String channelName = entry.getKey();
                List<Double> values = entry.getValue();

                // 确保只处理最新的 MAX_DATA_POINTS 数据点
                if (values.size() > MAX_DATA_POINTS) {
                    values = values.subList(values.size() - MAX_DATA_POINTS, values.size());
                }

                List<Entry> entries = new ArrayList<>();
                for (int i = 0; i < values.size(); i++) {
                    entries.add(new Entry(i, values.get(i).floatValue()));
                }

                LineDataSet dataSet = new LineDataSet(entries, channelName);
                dataSet.setDrawCircles(false);
                dataSet.setLineWidth(2f);
                dataSet.setDrawValues(false);

                // 动态生成随机颜色
                int randomColor = Color.rgb(
                        (int) (Math.random() * 256), // 随机生成红色分量
                        (int) (Math.random() * 256), // 随机生成绿色分量
                        (int) (Math.random() * 256)  // 随机生成蓝色分量
                );
                dataSet.setColor(randomColor);

                lineData.addDataSet(dataSet);
            }

            lineChart.setData(lineData);
            lineChart.invalidate();
        });
    }


    // home_state页面中的脑机接口图片
    public void getCircleImg(View view) {
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



