package com.example.pokeremotionapplication.data;

import static android.content.ContentValues.TAG;


import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.example.pokeremotionapplication.data.pojo.APreDataPoint;
import com.example.pokeremotionapplication.data.pojo.DataPoint;
import com.example.pokeremotionapplication.util.CSVUtil;
import com.example.pokeremotionapplication.util.ResultStr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataReadingService extends Service {

    private Thread readingThread;
    private List<APreDataPoint> newData = new ArrayList<>();
    private Map<String, List<Double>> channels;
    private volatile boolean running = true;
    private final int MAX_DATA_POINTS = 1000; // 展示1000个数据点
    private final int UPDATE_INTERVAL = 500; // 0.5 秒更新一次数据
    private final int UPDATE_DATA_POINTS = 250; // 每次更新250个数据点


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service", "成功连接设备");

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

        // 启动后台线程读取数据
        startReadingData();
    }

    // 启动后台线程读取数据
    public void startReadingData(){
        readingThread = new Thread(() -> {
            int csvFileIndex = 0;
            while(running){
                try {
                    String fileName = "data/data (" + (csvFileIndex % 200 + 1) + ").csv";

                    // 获取新的250个数据点
                    newData = readData(fileName);


                    // 更新 channels
                    for (APreDataPoint dataPoint : newData) {
                        Map<String, List<Double>> dataChannels = dataPoint.getChannels();
                        for (Map.Entry<String, List<Double>> entry : dataChannels.entrySet()) {
                            String channelName = entry.getKey();
                            List<Double> values = entry.getValue();

                            List<Double> channelData = channels.get(channelName);
                            if (channelData == null) {
                                channelData = new ArrayList<>();
                                channels.put(channelName, channelData);
                            }

                            // 添加新数据并限制数据集大小
                            channelData.addAll(values);
                            if (channelData.size() > MAX_DATA_POINTS) {
                                channelData = channelData.subList(channelData.size() - MAX_DATA_POINTS, channelData.size());
                            }

                            // 重新赋值给 channels
                            channels.put(channelName, channelData);
                        }
                    }

                    // 通知数据更新
                    notifyDataUpdated();

                    // 模拟每 0.5 秒读取一次
                    Thread.sleep(UPDATE_INTERVAL);

                    // 更新 CSV 文件索引
                    csvFileIndex++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        readingThread.start();
    }

    // 广播器通知数据更新
    private void notifyDataUpdated() {
        Intent intent = new Intent("com.example.pokeremotionapplication.DATA_UPDATED");
        intent.putExtra("channels", (Serializable) channels); // 将 channels 作为 Serializable 传递
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    // 读数据
    public List<APreDataPoint> readData(String fileName){
        // 读取CSV文件
        List<DataPoint> dataPoints = CSVUtil.readCSV(this, fileName);

        // 将List<DataPoint>转换为JSON字符串
        String jsonData = dataPoints.toString();

        // 获取Python模块
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("data");

        // 调用数据预处理方法
        PyObject result_pre =  pyObject.callAttr("preprocess_data" , jsonData);

        String resultStr = result_pre.toString();
        // Log.d("PythonResult" , resultStr);

        return ResultStr.readStr(resultStr);
    }
}
