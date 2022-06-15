package com.example.kafkademo.demo;

import com.alibaba.fastjson.JSONObject;
import com.example.kafkademo.data.PositionData;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PositionAlarm {

    static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 获取”应该生成的仓位“
    static List<JSONObject> planedPosition = PositionData.planedPosition;

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "positionAlarm");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        KTable<String, String> tableActual = builder.table("positionActual", Consumed.with(Topology.AutoOffsetReset.LATEST));
        KStream<String, String> tableHeartbeat = builder.stream("heartbeat", Consumed.with(Topology.AutoOffsetReset.LATEST));

        // 1. 根据心跳信号，与计划仓位生成表进行对比，得到超时仓位数据：超时3分钟以内、超时3分钟以上、超时6分钟以上
        KTable<String, String> suspected = tableHeartbeat.flatMap(keyValueMapper(0)).toTable();
        KTable<String, String> warning = tableHeartbeat.flatMap(keyValueMapper(3)).toTable();
        KTable<String, String> error = tableHeartbeat.flatMap(keyValueMapper(6)).toTable();
        suspected.toStream().to("topic3");
        warning.toStream().to("topic4");
        error.toStream().to("topic5");

        // 2.

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
    }

    public static KeyValueMapper<String, String, Iterable<KeyValue<String, String>>> keyValueMapper(long minutes) {
        return (key, eventTime) -> {
            List<KeyValue<String, String>> rst = new ArrayList<>();
            for (JSONObject position : planedPosition) {
                try {
                    Date positionTime = formatter.parse(position.getString("eventTime"));
                    Date heartbeatTime = formatter.parse(eventTime);
                    if (positionTime.getTime() <= heartbeatTime.getTime() + minutes * 1000) {
                        rst.add(new KeyValue<>(position.getString("positionName"), position.toJSONString()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return rst;
        };
    }
}
