package com.example.kafkademo.service;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class OperationService {

    private static final Properties props = new Properties();

    static {
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafkaOperations");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    }

    public void copy(String srcTopic, String tgtTopic) {
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(srcTopic).to(tgtTopic);
        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);

        streams.start();
    }

    public void join(String[] srcTopics, String tgtTopic) {

    }

}
