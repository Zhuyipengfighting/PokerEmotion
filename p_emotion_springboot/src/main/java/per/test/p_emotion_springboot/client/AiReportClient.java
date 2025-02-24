package per.test.p_emotion_springboot.client;

import ai.AiReport.AIOutput;
import ai.AiReport.UserInput;
import ai.EmotionAnalyticsServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;

public class AiReportClient {
    private final ManagedChannel channel;
    private final EmotionAnalyticsServiceGrpc.EmotionAnalyticsServiceBlockingStub blockingStub;

    public AiReportClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext() // 测试环境使用，生产环境需启用TLS
                .build();
        this.blockingStub = EmotionAnalyticsServiceGrpc.newBlockingStub(channel);
    }

    public String generateReport(UserInput request) {
        try {
            AIOutput response = blockingStub
                    .withDeadlineAfter(30, TimeUnit.SECONDS)
                    .generateReport(request);
            return response.getReport();
        } catch (Exception e) {
            throw new RuntimeException("gRPC调用失败: " + e.getMessage(), e);
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}