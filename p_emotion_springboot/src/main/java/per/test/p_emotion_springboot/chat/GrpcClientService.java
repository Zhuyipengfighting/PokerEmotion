package per.test.p_emotion_springboot.chat;

import aiemtion.AiGuideServiceGrpc;
import aiemtion.Ai.AIResponse;

import aiemtion.Ai.UserInput;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class GrpcClientService {

    private final ManagedChannel channel;
    private final AiGuideServiceGrpc.AiGuideServiceStub asyncStub;

    public GrpcClientService() {
        // 连接到本地 gRPC 服务器，端口号 50051
        this.channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        this.asyncStub = AiGuideServiceGrpc.newStub(channel);
    }

    public void sendChatMessages(String sessionId, String input1, String emotion1, String input2, String emotion2) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        // 调用 gRPC 流式方法，处理服务端的响应
        StreamObserver<AIResponse> responseObserver = new StreamObserver<AIResponse>() {
            @Override
            public void onNext(AIResponse value) {
                System.out.println("AI Response: " + value.getOutput());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Chat completed.");
                latch.countDown();
            }
        };

        StreamObserver<UserInput> requestObserver = asyncStub.chatStream(responseObserver);

        // 发送第一条消息
        requestObserver.onNext(UserInput.newBuilder()
                .setSessionId(sessionId)
                .setInput(input1)
                .setEmotion(emotion1)
                .build());

        // 发送第二条消息
        requestObserver.onNext(UserInput.newBuilder()
                .setSessionId(sessionId)
                .setInput(input2)
                .setEmotion(emotion2)
                .build());

        // 结束发送
        requestObserver.onCompleted();

        latch.await(5, TimeUnit.SECONDS);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
