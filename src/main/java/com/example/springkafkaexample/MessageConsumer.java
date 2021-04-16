package com.example.springkafkaexample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageConsumer {

    @KafkaListener(topics = "TOPIC", groupId = "foo")
    public void consume(String message) {
        log.info("consumed message: {}", message);
    }
}
