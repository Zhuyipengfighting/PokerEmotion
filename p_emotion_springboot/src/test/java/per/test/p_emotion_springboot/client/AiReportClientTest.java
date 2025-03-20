package per.test.p_emotion_springboot.client;

import ai.AiReport.*;
import ai.EmotionAnalyticsServiceGrpc;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AiReportClientTest {
//
//    @Test
//    public void testGenerateReport() throws InterruptedException {
//        // 创建 AiReportClient 实例
//        AiReportClient client = new AiReportClient("localhost", 50050);
//
//        // 创建请求对象
//        UserInput userInput = UserInput.newBuilder()
//                .addData(EmotionData.newBuilder()
//                        .setDate("2025-02-20")
//                        .setMainEmotion("happy")
//                        .setMainClassRatio(0.8)
//                        .setSecondaryEmotion("neutral")
//                        .setSecondaryClassRatio(0.2)
//                        .build())
//                .build();
//
//        // 调用 generateReport 方法
//        String report = client.generateReport(userInput);
//
//        // 验证响应
//        assertNotNull(report);
//
//        // 关闭客户端
//        client.shutdown();
//    }
}