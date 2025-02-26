package com.example.pokeremotionapplication.ui.home.ui_fragment;

// home页面中的设备状态页面
import static android.content.ContentValues.TAG;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.example.pokeremotionapplication.R;
import com.example.pokeremotionapplication.data.pojo.DataPoint;
import com.example.pokeremotionapplication.util.CSVUtil;
import com.example.pokeremotionapplication.util.DataParser;
import com.example.pokeremotionapplication.util.JsonFileUtils;
import com.example.pokeremotionapplication.util.JsonUtil;
import com.github.mikephil.charting.charts.LineChart;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HomeStateFragment extends Fragment {

    private LineChart lineChart;
    private List<String> dataPaths = new ArrayList<>();
    private List<String[]> DATA = new ArrayList<>();
    private List<DataPoint> dataPoints;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_state, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCircleImg(view);

        // 读取CSV文件
        String fileName = "data/data (1).csv";
        List<DataPoint> dataPoints = CSVUtil.readCSV(getContext(), fileName);

        // 将List<DataPoint>转换为JSON字符串
        String jsonData = dataPoints.toString();

        // 获取Python模块
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("data");

        // 调用数据预处理方法
        PyObject result_pre =  pyObject.callAttr("preprocess_data" , jsonData);

        String resultStr = result_pre.toString();
        Log.d("PythonResult" , resultStr);

        // 调用情绪分类方法
        PyObject result_cla = pyObject.callAttr("preprocess_and_classify_emotion" , jsonData);


        String resultCla = result_cla.toString();
        Log.d("PythonResult", resultCla);


        // 绑定折线图
        lineChart = view.findViewById(R.id.home_state_lineChart);
        // 边框
        lineChart.setDrawBorders(true);
        // 是否可以缩放图标
        lineChart.setScaleEnabled(true);
        // 手指是否能移动图表
        lineChart.setDragEnabled(false);

        // 没有数据的样式
        lineChart.setNoDataText("请佩戴设备");


    }


    // 得到假数据
    public List<DataPoint> initDataPaths() {
        String jsonData = JsonFileUtils.loadJSONFromAsset(requireContext(), "data.json");
        return DataParser.parseData(jsonData);
    }


    //



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



