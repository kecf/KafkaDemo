package com.example.kafkademo.demo;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class SignalMonitor {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "signalMonitor");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
//        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> kStream = builder.stream("topic2", Consumed.with(Topology.AutoOffsetReset.LATEST));

        KTable<String, String> tablePrevDay = kStream.filter((key, value) -> {
            JSONObject jsonObject = JSONObject.parseObject(value);
            try {
                Date date = formatter.parse(jsonObject.getString("eventTime"));
                return date.getDay() == new Date().getDay() - 1;
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        })
                .mapValues((value) -> {
                    JSONObject jsonObject = JSONObject.parseObject(value);
                    jsonObject.put("type", "topic3");
                    return jsonObject.toJSONString();
                })
                .toTable();
        tablePrevDay.toStream().to("topic3");

        KTable<String, String> tableToday = kStream.filter((key, value) -> {
            JSONObject jsonObject = JSONObject.parseObject(value);
            try {
                Date date = formatter.parse(jsonObject.getString("eventTime"));
                return date.getDay() == new Date().getDay();
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        })
                .mapValues((value) -> {
                    JSONObject jsonObject = JSONObject.parseObject(value);
                    jsonObject.put("type", "topic4");
                    return jsonObject.toJSONString();
                })
                .toTable();
        tableToday.toStream().to("topic4");

        // leftjoin得到昨天有的，filter出今天没有的
        KTable<String, String> table = tablePrevDay.leftJoin(tableToday, (prevDay, today) -> prevDay + "----" + today);
        table.filter((key, value) -> value.contains("null"))
                .mapValues((value) -> {
                    JSONObject jsonObject = JSONObject.parseObject(value.split("----")[0]);
                    jsonObject.put("type", "topic5");
                    return jsonObject.toJSONString();
                })
                .toStream().to("topic5");

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);

        streams.start();
    }
}
