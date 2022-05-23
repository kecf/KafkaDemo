package com.example.kafkademo;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class KafkaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaDemoApplication.class, args);
    }

    @Bean
    public NewTopic newTopic() {
        return TopicBuilder.name("topic1")
                .partitions(10)
                .replicas(1)
                .build();
    }

    // define several topics one time
    // can omit partitions and replicas
//    @Bean
//    public KafkaAdmin.NewTopics newTopics() {
//        return new KafkaAdmin.NewTopics(
//                TopicBuilder.name("topic2")
//                        .build(),
//                TopicBuilder.name("topic3")
//                        .build()
//        );
//    }

    @KafkaListener(id = "myId", topics = "topic1")
    public void listen(String in) {
        System.out.println(in);
    }

    @Bean
    public ApplicationRunner runner(KafkaTemplate<String, String> template) {
        return args -> {
            for (int i = 0; i < 10; i++) {
                template.send("topic1", "message " + i);
                Thread.sleep(1000);
            }
        };
    }
}
