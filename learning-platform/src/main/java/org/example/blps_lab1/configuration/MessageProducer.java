package org.example.blps_lab1.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    private final KafkaTemplate<String, KafkaUser> kafkaTemplate;

    @Autowired
    public MessageProducer(KafkaTemplate<String, KafkaUser> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, KafkaUser message) {
        kafkaTemplate.send(topic, message);
    }

}