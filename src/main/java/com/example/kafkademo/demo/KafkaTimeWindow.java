package com.example.kafkademo.demo;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaTimeWindow {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafkaTimeWindow");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        Duration windowSize = Duration.ofSeconds(5);
        Duration advanceSize = Duration.ofSeconds(2);
        TimeWindows hoppingWindow = TimeWindows.of(windowSize);

        KStream<Windowed<String>, Long> countStream = builder
                .stream("topic3", Consumed.with(Serdes.String(), Serdes.String()))
                .groupBy((key, word) -> word, Grouped.with(Serdes.String(), Serdes.String()))
                .windowedBy(hoppingWindow)
                .count()
                .toStream();
        countStream.print(Printed.toSysOut());
        countStream.to("topic4");

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);

        streams.start();
    }
}
