package com.example.kafkademo.demo;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.internals.KTableImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class SignalMonitor {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "signalMonitor");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StreamsBuilder builder = new StreamsBuilder();
        KTable<String, String> kTable = builder.table("topic2");

        KTable<String, String> tablePrevDay = kTable.filter((key, value) -> {
            JSONObject jsonObject = JSONObject.parseObject(value);
            try {
                Date date = formatter.parse(jsonObject.getString("eventTime"));
                return date.getDay() == new Date().getDay() - 1;
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        });
        tablePrevDay.toStream().to("topic3");

        KTable<String, String> tableToday = kTable.filter((key, value) -> {
            JSONObject jsonObject = JSONObject.parseObject(value);
            try {
                Date date = formatter.parse(jsonObject.getString("eventTime"));
                return date.getDay() == new Date().getDay();
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        });

        tableToday.toStream().to("topic4");

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);

        streams.start();
    }
}
