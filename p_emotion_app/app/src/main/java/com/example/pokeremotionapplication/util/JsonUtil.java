package com.example.pokeremotionapplication.util;

import com.example.pokeremotionapplication.data.pojo.DataPoint;
import com.google.gson.Gson;
import java.util.List;

public class JsonUtil {

    public static String toJson(List<DataPoint> dataPoints) {
        Gson gson = new Gson();
        return gson.toJson(dataPoints);
    }
}
