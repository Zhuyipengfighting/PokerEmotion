package per.test.p_emotion_springboot.chat;

import per.test.p_emotion_springboot.chat.GrpcClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GrpcClientController {

    private final GrpcClientService grpcClientService;

    public GrpcClientController(GrpcClientService grpcClientService) {
        this.grpcClientService = grpcClientService;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String sessionId,
                       @RequestParam String input1,
                       @RequestParam String emotion1,
                       @RequestParam String input2,
                       @RequestParam String emotion2) {
        try {
            grpcClientService.sendChatMessages(sessionId, input1, emotion1, input2, emotion2);
            return "请求已发送，请查看控制台输出。";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "发生错误：" + e.getMessage();
        }
    }
}