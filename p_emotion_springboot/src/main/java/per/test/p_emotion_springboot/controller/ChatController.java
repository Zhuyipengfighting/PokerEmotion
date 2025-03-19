package per.test.p_emotion_springboot.controller;

import aiemtion.Ai;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import per.test.p_emotion_springboot.client.AiGrpcClient;
import aiemtion.Ai.AIResponse;
import aiemtion.Ai.UserInput;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AiGrpcClient aiGrpcClient;

    @Autowired
    public ChatController(AiGrpcClient aiGrpcClient) {
        this.aiGrpcClient = aiGrpcClient;
    }

    @GetMapping("/stream")
    public SseEmitter streamChat(@RequestParam String sessionId) {
        SseEmitter emitter = new SseEmitter();

        // 启动流式通信
        StreamObserver<UserInput> requestObserver = aiGrpcClient.startChat(
                new StreamObserver<AIResponse>() {
                    @Override
                    public void onNext(AIResponse response) {
                        try {
                            emitter.send(SseEmitter.event().data(response.getOutput()));
                        } catch (IOException e) {
                            onError(e);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        emitter.completeWithError(t);
                    }

                    @Override
                    public void onCompleted() {
                        emitter.complete();
                    }
                });

        // 例子：发送一条消息到 gRPC 服务端
        requestObserver.onNext(
                UserInput.newBuilder()
                        .setSessionId(sessionId)
                        .setInput("Hello")
                        .setEmotion("happy")
                        .build());

        // 开启超时机制，防止连接泄露
        emitter.onCompletion(() -> requestObserver.onCompleted());
        emitter.onTimeout(() -> {
            emitter.complete();
            requestObserver.onCompleted();
        });

        return emitter;
    }
}
