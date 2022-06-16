package com.example.kafkademo.demo;

import com.alibaba.fastjson.JSONObject;
import com.example.kafkademo.data.PositionData;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;

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
        KStream<String, String> stream = builder.stream("topic2", Consumed.with(Topology.AutoOffsetReset.LATEST));
        KStream<String, String> streamHeartbeat = stream.filter((key, value) -> Objects.equals(key, "heartbeat"));
        KTable<String, String> tableActual = stream.filter((key, value) -> !Objects.equals(key, "heartbeat")).toTable();

        // 1. 根据心跳信号，与计划仓位生成表进行对比，得到预期超时仓位数据：超时1分钟以上、超时4分钟以上、超时7分钟以上
        KTable<String, String> suspectedTemp = streamHeartbeat.flatMap(keyValueMapper(1, 4)).toTable();
        KTable<String, String> warningTemp = streamHeartbeat.flatMap(keyValueMapper(4, 7)).toTable();
        KTable<String, String> errorTemp = streamHeartbeat.flatMap(keyValueMapper(7, 100000)).toTable();

        // 2. 将超时仓位和已生成仓位进行join，得到实际超时仓位
        //TODO: 把下面的重复代码整合为一个方法
        KTable<String, String> suspected = suspectedTemp.leftJoin(tableActual, (v1, v2) -> v1 + "----" + v2)
                .toStream()
                .filter((key, value) -> value.contains("null"))
                .toTable()
                .mapValues((value) -> {
                    JSONObject jsonObject = JSONObject.parseObject(value.split("----")[0]);
                    jsonObject.put("type", "suspected");
                    return jsonObject.toJSONString();
                });
        KTable<String, String> warning = warningTemp.leftJoin(tableActual, (v1, v2) -> v1 + "----" + v2)
                .toStream()
                .filter((key, value) -> value.contains("null"))
                .toTable()
                .mapValues((value) -> {
                    JSONObject jsonObject = JSONObject.parseObject(value.split("----")[0]);
                    jsonObject.put("type", "warning");
                    return jsonObject.toJSONString();
                });
        KTable<String, String> error = errorTemp.leftJoin(tableActual, (v1, v2) -> v1 + "----" + v2)
                .toStream()
                .filter((key, value) -> value.contains("null"))
                .toTable()
                .mapValues((value) -> {
                    JSONObject jsonObject = JSONObject.parseObject(value.split("----")[0]);
                    jsonObject.put("type", "error");
                    return jsonObject.toJSONString();
                });

        // 3. 将三个table发给对应的topic
        streamHeartbeat.to("heartbeat");
        suspected.toStream().to("topic3");
        warning.toStream().to("topic4");
        error.toStream().to("topic5");

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();
    }

    public static KeyValueMapper<String, String, Iterable<KeyValue<String, String>>> keyValueMapper(long from, long to) {
        return (key, heartbeat) -> {
            List<KeyValue<String, String>> rst = new ArrayList<>();
            for (JSONObject position : planedPosition) {
                try {
                    Date positionTime = formatter.parse(position.getString("eventTime"));
                    Date heartbeatTime = formatter.parse(JSONObject.parseObject(heartbeat).getString("eventTime"));
                    if (positionTime.getTime() + from * 60 * 1000 <= heartbeatTime.getTime() && heartbeatTime.getTime() < positionTime.getTime() + to * 60 * 1000) {
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
