package per.test.p_emotion_springboot.model;

import ai.AiReport.UserInput;
import ai.AiReport.EmotionData;
import ai.AiReport.*;

import java.util.ArrayList;
import java.util.List;

public class JsonUserInput {

    private List<EmotionDataJson> input = new ArrayList<>();
    public List<EmotionDataJson> getInput() {
        return input;
    }

    public void setInput(List<EmotionDataJson> input) {
        this.input = input;
    }

    // 将 JSON 解析的 Java Bean 转换为 Protobuf UserInput
    public UserInput toProto() {
        UserInput.Builder builder = UserInput.newBuilder();
        for (EmotionDataJson data : input) {
            builder.addData(EmotionData.newBuilder()
                    .setDate(data.getDate())
                    .setMainEmotion(data.getMainEmotion())
                    .setMainClassRatio(data.getMainClassRatio())
                    .setSecondaryEmotion(data.getSecondaryEmotion())
                    .setSecondaryClassRatio(data.getSecondaryClassRatio())
                    .build());
        }
        return builder.build();
    }
}
