package per.test.p_emotion_springboot.controller;

import aiemtion.Ai;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import per.test.p_emotion_springboot.client.AiGrpcClient;
import aiemtion.Ai.AIResponse;
import aiemtion.Ai.UserInput;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AiGrpcClient aiGrpcClient;
    private final ConcurrentHashMap<String, StreamObserver<UserInput>> sessionObservers = new ConcurrentHashMap<>();
    @Autowired
    public ChatController(AiGrpcClient aiGrpcClient) {
        this.aiGrpcClient = aiGrpcClient;
    }

    @GetMapping("/stream")
    public SseEmitter streamChat(@RequestParam String sessionId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // 设置超时时间为最大值

        // 启动流式通信
        StreamObserver<UserInput> requestObserver = aiGrpcClient.startChat(
                new StreamObserver<AIResponse>() {
                    @Override
                    public void onNext(AIResponse response) {

                        try {
                            emitter.send(SseEmitter.event().data(response.getOutput()));

                        } catch (IOException e) {
                            emitter.completeWithError(e);
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

        // 保存 StreamObserver
        sessionObservers.put(sessionId, requestObserver);

        // 在连接关闭时调用 gRPC 的 onCompleted
        emitter.onCompletion(() -> {
            requestObserver.onCompleted();
            sessionObservers.remove(sessionId);
        });
        emitter.onTimeout(() -> {
            emitter.complete();
            requestObserver.onCompleted();
            sessionObservers.remove(sessionId);
        });

        return emitter;
    }

    @PostMapping("/send")
    public void sendChatMessage(@RequestBody ChatMessage message) {
        // 获取对应的 StreamObserver
        StreamObserver<UserInput> requestObserver = sessionObservers.get(message.getSessionId());
        if (requestObserver != null) {
            System.out.println("Received message from client: " + message.getInput());
            // 发送消息到 gRPC 服务端
            requestObserver.onNext(
                    UserInput.newBuilder()
                            .setSessionId(message.getSessionId())
                            .setInput(message.getInput())
                            .setEmotion(message.getEmotion())
                            .build()
            );
        } else {
            // 如果没有找到对应的 StreamObserver，可以记录日志或返回错误
            System.err.println("No active session found for sessionId: " + message.getSessionId());
        }
    }
}
