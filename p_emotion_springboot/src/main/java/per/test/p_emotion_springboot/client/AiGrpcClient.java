package per.test.p_emotion_springboot.client;
import ai.EmotionAnalyticsServiceGrpc;

import aiemtion.AiGuideServiceGrpc;
import aiemtion.Ai.UserInput;
import aiemtion.Ai.AIResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class AiGrpcClient {
    private final ManagedChannel channel;
    private final AiGuideServiceGrpc.AiGuideServiceStub asyncStub;

    public AiGrpcClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.asyncStub = AiGuideServiceGrpc.newStub(channel);
    }

    public StreamObserver<UserInput> startChat(StreamObserver<AIResponse> responseObserver) {
        return asyncStub.chatStream(responseObserver);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}