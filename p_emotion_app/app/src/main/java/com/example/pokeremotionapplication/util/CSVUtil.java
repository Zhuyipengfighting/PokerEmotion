package com.example.pokeremotionapplication.util;

import android.content.Context;
import android.content.res.AssetManager;
import com.example.pokeremotionapplication.data.pojo.DataPoint;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {

    public static List<DataPoint> readCSV(Context context, String fileName) {
        List<DataPoint> dataPoints = new ArrayList<>();

        try (InputStream is = context.getAssets().open(fileName);
             CSVReader reader = new CSVReader(new InputStreamReader(is))) {

            String[] nextLine;
            int index = 0; // 用于生成索引
            reader.readNext(); // 跳过标题行
            while ((nextLine = reader.readNext()) != null) {
                // 解析每一行的数据
                double channel1 = Double.parseDouble(nextLine[0]);
                double channel2 = Double.parseDouble(nextLine[1]);
                double channel3 = Double.parseDouble(nextLine[2]);
                double channel4 = Double.parseDouble(nextLine[3]);
                double channel5 = Double.parseDouble(nextLine[4]);
                double channel6 = Double.parseDouble(nextLine[5]);
                double channel7 = Double.parseDouble(nextLine[6]);
                double channel8 = Double.parseDouble(nextLine[7]);
                double channel9 = Double.parseDouble(nextLine[8]);
                double channel10 = Double.parseDouble(nextLine[9]);
                double channel11 = Double.parseDouble(nextLine[10]);
                double channel12 = Double.parseDouble(nextLine[11]);
                double channel13 = Double.parseDouble(nextLine[12]);
                double channel14 = Double.parseDouble(nextLine[13]);
                double channel15 = Double.parseDouble(nextLine[14]);
                double channel16 = Double.parseDouble(nextLine[15]);

                // 创建DataPoint对象并添加到列表
                DataPoint dataPoint = new DataPoint(index, channel1, channel2, channel3, channel4,
                        channel5, channel6, channel7, channel8, channel9, channel10,
                        channel11, channel12, channel13, channel14, channel15, channel16);
                dataPoints.add(dataPoint);
                index++; // 索引递增
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return dataPoints;
    }
}
