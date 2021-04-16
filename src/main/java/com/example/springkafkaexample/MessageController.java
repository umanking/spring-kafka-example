package com.example.springkafkaexample;

import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class MessageController {

    private final MessageProducer messageProducer;

    @PostMapping
    public String sendMessage(@RequestParam("message") String message) {
        messageProducer.sendMessage(message);
        return "success";
    }
}
