package per.test.p_emotion_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PEmotionSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(PEmotionSpringbootApplication.class, args);

        System.out.println("************************************************************************\n"
                            +"****************************智联后端项目启动成功****************************\n"+
                                "************************************************************************");
    }

}
