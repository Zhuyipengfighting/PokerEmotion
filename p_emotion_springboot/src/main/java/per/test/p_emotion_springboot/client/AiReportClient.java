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
                .usePlaintext()
                .build();
        this.blockingStub = EmotionAnalyticsServiceGrpc.newBlockingStub(channel);
    }

    public AIOutput generateReport(UserInput request) {
        try {
            // **直接返回 AIOutput 对象，而不是 String**
            return blockingStub
                    .withDeadlineAfter(30, TimeUnit.SECONDS)
                    .generateReport(request);
        } catch (Exception e) {
            throw new RuntimeException("gRPC调用失败: " + e.getMessage(), e);
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}