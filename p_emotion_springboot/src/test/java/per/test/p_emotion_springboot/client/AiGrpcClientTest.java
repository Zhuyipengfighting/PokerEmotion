package per.test.p_emotion_springboot.client;

import aiemtion.Ai.UserInput;
import aiemtion.Ai.AIResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AiGrpcClientTest {
    @Test
    public void testGrpcClient() throws InterruptedException {
        AiGrpcClient client = new AiGrpcClient("localhost", 50051);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AIResponse> responseObserver = new StreamObserver<AIResponse>() {
            @Override
            public void onNext(AIResponse response) {
                assertNotNull(response.getOutput());
                latch.countDown();
            }
            @Override
            public void onError(Throwable t) { latch.countDown(); }
            @Override
            public void onCompleted() { latch.countDown(); }
        };

        StreamObserver<UserInput> requestObserver = client.startChat(responseObserver);
        requestObserver.onNext(UserInput.newBuilder()
                .setSessionId("test-session")
                .setInput("Hello AI")
                .setEmotion("neutral")
                .build());
        latch.await();
        client.shutdown();
    }
}