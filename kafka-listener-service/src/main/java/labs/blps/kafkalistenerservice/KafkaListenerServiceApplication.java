package labs.blps.kafkalistenerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KafkaListenerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaListenerServiceApplication.class, args);
    }

}
