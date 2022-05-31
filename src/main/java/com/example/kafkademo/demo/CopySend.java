package com.example.kafkademo.demo;

import com.example.kafkademo.service.OperationService;
import com.example.kafkademo.service.WebSocketService;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;

public class CopySend {
    public static void main(String[] args) {
        new OperationService().copySend("topic2", "topic4");
    }
}
