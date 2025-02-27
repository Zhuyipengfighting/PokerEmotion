package com.example.pokeremotionapplication.util;

import com.example.pokeremotionapplication.data.pojo.APreDataPoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ResultStr {
    private List<APreDataPoint> result;

    public ResultStr() {
        this.result = new ArrayList<>();
    }
    public static List<APreDataPoint> readStr(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<APreDataPoint> result = new ArrayList<>();

        try {
            // 将 JSON 字符串解析为 JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // 遍历 JSON 的每个字段（每个通道）
            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String channelName = entry.getKey(); // 通道名称
                JsonNode channelNode = entry.getValue(); // 通道对应的值

                List<Double> channelValues = new ArrayList<>();
                // 将通道的值解析为 List<Double>
                for (int i = 0; i < channelNode.size(); i++) {
                    channelValues.add(channelNode.get(i).asDouble());
                }

                // 创建一个 Map 存储当前通道的数据
                Map<String, List<Double>> channels = new HashMap<>();
                channels.put(channelName, channelValues);

                // 创建 APreDataPoint 实例并添加到结果列表
                result.add(new APreDataPoint(channels));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
