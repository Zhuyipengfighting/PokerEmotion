package per.test.p_emotion_springboot.controller;

import ai.AiReport.AIOutput;
import ai.AiReport.UserInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.test.p_emotion_springboot.client.AiReportClient;
import per.test.p_emotion_springboot.model.JsonUserInput;
import per.test.p_emotion_springboot.model.*;

@RestController
@RequestMapping("/api/emotion")
public class EmotionAnalyticsController {

    private final AiReportClient aiReportClient;

    @Autowired
    public EmotionAnalyticsController(AiReportClient aiReportClient) {
        this.aiReportClient = aiReportClient;
    }

    @PostMapping(value = "/generate-report", consumes = "application/json", produces = "application/json")
    public JsonAIOutput generateReport(@RequestBody JsonUserInput jsonUserInput) {
        if (jsonUserInput == null || jsonUserInput.getInput() == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }

        // JSON -> Protobuf
        UserInput userInput = jsonUserInput.toProto();

        // 调用 gRPC 服务
        AIOutput aiOutput = aiReportClient.generateReport(userInput);

        // **转换 Protobuf AIOutput -> JSON 友好的 Java Bean**
        return new JsonAIOutput(aiOutput);
    }}
