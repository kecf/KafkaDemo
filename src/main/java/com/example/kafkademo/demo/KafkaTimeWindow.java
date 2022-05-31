package com.example.kafkademo.demo;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;

import java.sql.Time;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaTimeWindow {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafkaTimeWindow");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
//        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 3000);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass());

        StreamsBuilder builder = new StreamsBuilder();

//        Duration windowSize = Duration.ofSeconds(5);
//        Duration advanceSize = Duration.ofSeconds(2);
//        TimeWindows hoppingWindow = TimeWindows.of(windowSize).advanceBy(advanceSize);
        TimeWindows hoppingWindow = TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(5));

        builder.stream("topic3", Consumed.with(Serdes.String(), Serdes.String()))
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split(" ")))
                .groupBy((key, word) -> word, Grouped.with(Serdes.String(), Serdes.String()))
                .windowedBy(hoppingWindow)
                .count()
                .toStream()
                .map((key, value) -> KeyValue.pair(key.toString(), value))
                .to("topic4");

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);

        streams.start();
    }
}
