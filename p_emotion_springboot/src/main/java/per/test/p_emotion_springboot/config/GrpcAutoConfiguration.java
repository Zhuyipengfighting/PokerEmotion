package per.test.p_emotion_springboot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import per.test.p_emotion_springboot.client.AiGrpcClient;
import per.test.p_emotion_springboot.client.AiReportClient;
@Configuration
public class GrpcAutoConfiguration {

    @Value("${grpc.server.host:localhost}")
    private String host;

    @Value("${grpc.server.port:50051}")
    private int port;

    @Bean
    public AiGrpcClient aiGrpcClient() {
        return new AiGrpcClient(host, port);
    }

    @Value("${grpc.report.host:localhost}")
    private String reportHost;

    @Value("${grpc.report.port:50050}")
    private int reportPort;

    @Bean
    public AiReportClient aiReportClient() {
        return new AiReportClient(reportHost, reportPort);
    }

}
