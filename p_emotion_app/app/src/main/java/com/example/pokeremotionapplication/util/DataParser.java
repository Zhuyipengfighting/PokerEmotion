package com.example.pokeremotionapplication.util;

import com.example.pokeremotionapplication.data.pojo.DataPoint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// 处理json数据
public class DataParser {

    public static List<DataPoint> parseData(String jsonData) {
        List<DataPoint> dataPoints = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // 解析json
                int index = Integer.parseInt(jsonObject.optString("", "0"));
                double channel1 = Double.parseDouble(jsonObject.optString("Channel_1", "0.0"));
                double channel2 = Double.parseDouble(jsonObject.optString("Channel_2", "0.0"));
                double channel3 = Double.parseDouble(jsonObject.optString("Channel_3", "0.0"));
                double channel4 = Double.parseDouble(jsonObject.optString("Channel_4", "0.0"));
                double channel5 = Double.parseDouble(jsonObject.optString("Channel_5", "0.0"));
                double channel6 = Double.parseDouble(jsonObject.optString("Channel_6", "0.0"));
                double channel7 = Double.parseDouble(jsonObject.optString("Channel_7", "0.0"));
                double channel8 = Double.parseDouble(jsonObject.optString("Channel_8", "0.0"));
                double channel9 = Double.parseDouble(jsonObject.optString("Channel_9", "0.0"));
                double channel10 = Double.parseDouble(jsonObject.optString("Channel_10", "0.0"));
                double channel11 = Double.parseDouble(jsonObject.optString("Channel_11", "0.0"));
                double channel12 = Double.parseDouble(jsonObject.optString("Channel_12", "0.0"));
                double channel13 = Double.parseDouble(jsonObject.optString("Channel_13", "0.0"));
                double channel14 = Double.parseDouble(jsonObject.optString("Channel_14", "0.0"));
                double channel15 = Double.parseDouble(jsonObject.optString("Channel_15", "0.0"));
                double channel16 = Double.parseDouble(jsonObject.optString("Channel_16", "0.0"));

                DataPoint dataPoint = new DataPoint(
                        index, channel1, channel2, channel3, channel4, channel5,
                        channel6, channel7, channel8, channel9, channel10,
                        channel11, channel12, channel13, channel14, channel15,
                        channel16
                );

                dataPoints.add(dataPoint);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataPoints;
    }
}