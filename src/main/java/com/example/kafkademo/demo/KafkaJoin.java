package com.example.kafkademo.demo;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueJoiner;

import java.time.Duration;
import java.util.Properties;

public class KafkaJoin {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafkaJoin");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> left = builder.stream("topic2");
        KStream<String, String> right = builder.stream("topic3");
        ValueJoiner<String, String, String> valueJoiner = (leftValue, rightValue) -> leftValue + rightValue;

        left.toTable().outerJoin(right.toTable(), valueJoiner)
                .toStream()
                .to("topic4");

//        KStream<String, String> joined = left.outerJoin(right,
//                valueJoiner,
//                JoinWindows.of(Duration.ofSeconds(2)));
//
//        joined.to("topic4");

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);

        streams.start();
    }
}
