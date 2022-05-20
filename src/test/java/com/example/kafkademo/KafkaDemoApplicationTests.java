package com.example.kafkademo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.io.IOException;

@SpringBootTest
@EmbeddedKafka(count = 1, ports = {9092})
class KafkaDemoApplicationTests {

    @Autowired
    private KafkaTemplate<Object, Object> template;

    @KafkaListener(id = "listner1", topics = "test_topic1")
    void listen(ConsumerRecord<?, ?> record) {

    }

    @Test
    void contextLoads(){
        template.send("test_topic1", "test_message1");
    }

}
