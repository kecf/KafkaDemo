package com.example.kafkademo;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
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

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class KafkaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaDemoApplication.class, args);
    }

    @Bean
    public NewTopic newTopic() {
        return TopicBuilder.name("topic1")
                .partitions(1)
                .replicas(1)
                .build();
    }

    // define several topics one time
    // can omit partitions and replicas
    @Bean
    public KafkaAdmin.NewTopics newTopics() {
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name("topic2")
                        .build(),
                TopicBuilder.name("topic3")
                        .build(),
                TopicBuilder.name("topic4")
                        .build()
        );
    }

    @KafkaListener(id = "myId3", topics = "topic4")
    public void listen(String in) {
        System.out.println(in);
    }

    @Bean
    public ApplicationRunner runner(KafkaTemplate<String, String> template) {
        return args -> {
            for (int i = 0; i < 20; i++) {
                template.send("topic2", "key1", "message1 " + i);
                template.send("topic2", "key2", "message2 " + i);
                template.send("topic3", "key3", "message3 " + i);
                template.send("topic3", "key4", "message4 " + i);
                Thread.sleep(1000);
            }
        };
    }
}
