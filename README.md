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

## 4. Producer, Consumer 구성 
```java
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
