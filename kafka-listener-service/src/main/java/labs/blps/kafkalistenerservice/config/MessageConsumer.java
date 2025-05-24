package labs.blps.kafkalistenerservice.config;

import labs.blps.kafkalistenerservice.model.User;
import labs.blps.kafkalistenerservice.service.RegisterService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MessageConsumer {
    private final RegisterService registerService;


    @KafkaListener(topics = "reg-users", groupId = "group-id")
    public void listen(User user) {
        System.out.println("Received message: " + user.toString());
        registerService.register(user);
    }

}