package per.test.p_emotion_springboot.controller;

import ai.AiReport.UserInput;
import ai.AiReport.AIOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import per.test.p_emotion_springboot.client.AiReportClient;
import per.test.p_emotion_springboot.common.BusinessException;

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

    @GetMapping("/test-error")
    // 手抛出异常
    public void testError() {
        throw new BusinessException(1001, "测试业务异常");
    }
}

//@Data
//class ReportRequest {
//    @NotBlank(message = "日期不能为空")
//    private String date;
//
//    @Min(value = 0, message = "情绪值不能小于0")
//    private double emotionValue;
//}