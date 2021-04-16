package com.example.springkafkaexample;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducer {

    private static final String TOPIC = "TOPIC";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        log.info("produce message:{}", message);

        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, message);
        // get()을 호출하면 block 되고, producer가 그럴 필요가 없다.  (동기)
        // addCallBack을 통해서 파라미터로 ListenableFutureCallback을 넘겨주는 것이 훨씬 이득 (비동기)
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("unable to send message, due to: {}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult) {
                log.info("sent message, with offset: {}", sendResult.getRecordMetadata().offset());
            }
        });
    }
}
