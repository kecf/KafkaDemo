package com.example.kafkademo;

import com.alibaba.fastjson.JSONObject;
import com.example.kafkademo.controller.WebSocketController;
import com.example.kafkademo.data.EventData;
import com.example.kafkademo.service.WebSocketService;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SpringBootApplication
public class KafkaDemoApplication {

    @Autowired
    WebSocketService webSocketService;

    public static void main(String[] args) {
        SpringApplication.run(KafkaDemoApplication.class, args);
    }

//    @Bean
//    public NewTopic newTopic() {
//        return TopicBuilder.name("topic1")
//                .partitions(1)
//                .replicas(1)
//                .build();
//    }
//
    // define several topics one time
    // can omit partitions and replicas
//    @Bean
//    public KafkaAdmin.NewTopics newTopics() {
//        return new KafkaAdmin.NewTopics(
//                TopicBuilder.name("topic2")
//                        .build(),
//                TopicBuilder.name("topic3")
//                        .build(),
//                TopicBuilder.name("topic4")
//                        .build(),
//                TopicBuilder.name("topic5")
//                        .build()
//        );
//    }

    @KafkaListener(groupId = "StringValueID", topics = {"topic3", "topic4", "topic5"})
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("offset: " + record.offset() + " topic: " + record.topic() + " key: " + record.key() + " value: " + record.value());
//        webSocketService.sendJson("kcf", record.key(), record.value());
        webSocketService.sendMessage("kcf", record.value());
    }

    @Bean
    public ApplicationRunner runner(KafkaTemplate<String, String> template) {
        return args -> {
            for (JSONObject jsonObject : EventData.events) {
                template.send("topic2", jsonObject.getString("signalName"), jsonObject.toJSONString());
                System.out.println("----------------------");
                Thread.sleep(1000);
            }
        };
    }
}
