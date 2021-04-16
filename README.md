# spring-kafka-example

## 1. docker kafka, zookeeper install 

프로젝트 root에 `docker-compose.yml`파일 작성 
```properties
version: '2'
services:
  zookeeper:
    image: wurstmeister/zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
  kafaka:
    image: wurstmeister/kafka
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: 127.0.0.1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```

```shell script
docker-compose up -d 
docker container ls  
```



## 2. 라이브러리 추가 
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.kafka</groupId>
  <artifactId>spring-kafka</artifactId>
</dependency>
```

## 3. application.yml 파일 설정 
```yml
spring:
  kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: foo
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      bootstrap-servers: localhost:9092
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```
- auto-offset-rest
    - offset 정보가 없어졌을때, 어떻게 offset을 reset할 것인지 
        - lastest: 가장 최근에 생산된 메세지로 offset을 reset한다.
        - earliest: 가장 오랜된(초기에) 메세지로 offset을 reset 한다.
        - none: offset 정보가 없으면 Exception을 발생한다.     



## 4. Producer, Consumer 구성 
```java
public class MessageProducer {

    private static final String TOPIC = "TOPIC";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) throws ExecutionException, InterruptedException {
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
```


```java
@Slf4j
@Service
public class MessageConsumer {

    @KafkaListener(topics = "TOPIC", groupId = "foo")
    public void consume(String message) {
        log.info("consumed message: {}", message);
    }
}
```
## 5. Controller 구성
```java
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class KafkaController {

    private final MessageProducer messageProducer;

    @PostMapping
    public String sendMessage(@RequestParam("message") String message) throws ExecutionException, InterruptedException {
        messageProducer.sendMessage(message);
        return "success";
    }
}
```

## 6. 테스트 
```shell script
$ curl -XPOST http://localhost:8080/kafka -d "message=hello"
$ docker exec -it kafka bash 
bash# kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic TOPIC
```
