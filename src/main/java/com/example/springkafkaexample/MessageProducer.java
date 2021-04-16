package com.example.springkafkaexample;

import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducer {

    private static final String TOPIC = "TOPIC";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) throws ExecutionException, InterruptedException {
        log.info("produce message:{}", message);

        // todo: message 보내고 나서, 확인은 무엇으로 하나?
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, message);
        SendResult<String, String> sendResult = future.get();
        log.info("sendResult:{}", sendResult);
    }
}
