package per.test.p_emotion_springboot.controller;

import ai.AiReport.UserInput;
import ai.AiReport.AIOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import per.test.p_emotion_springboot.client.AiReportClient;

@RestController
@RequestMapping("/api/emotion")
public class EmotionAnalyticsController {

    private final AiReportClient aiReportClient;

    @Autowired
    public EmotionAnalyticsController(AiReportClient aiReportClient) {
        this.aiReportClient = aiReportClient;
    }

    @PostMapping("/generate-report")
    public AIOutput generateReport(@RequestBody UserInput userInput) {
        return AIOutput.newBuilder()
                .setReport(aiReportClient.generateReport(userInput))
                .build();
    }
}