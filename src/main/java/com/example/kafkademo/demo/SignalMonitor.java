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
import org.apache.kafka.streams.kstream.Printed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

public class SignalMonitor {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "signalMonitor");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
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
                    jsonObject.put("type", "prevDay");
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
                    jsonObject.put("type", "today");
                    return jsonObject.toJSONString();
                })
                .toTable();
        tableToday.toStream().to("topic4");

        // leftjoin得到昨天有的，mapvalues分为3类
        KTable<String, String> table = tablePrevDay.leftJoin(tableToday, (prevDay, today) -> prevDay + "----" + today);
        table.mapValues((value) -> {
            JSONObject jsonPrevDay = JSONObject.parseObject(value.split("----")[0]);
            if (Objects.equals(value.split("----")[1], "null")) {
                // 1. 如果table1有，table2没有，且当前时间大于table1中对应信号的时间，则type设为error
                try {
                    Date timePrevDay = formatter.parse(jsonPrevDay.getString("eventTime"));
                    if (timePrevDay.getTime() < new Date().getTime() - 60*1000) {
                        jsonPrevDay.put("type", "error");
                    } else {
                        jsonPrevDay.put("type", "tbd");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                JSONObject jsonToday = JSONObject.parseObject(value.split("----")[1]);
                try {
                    Date timePrevDay = formatter.parse(jsonPrevDay.getString("eventTime"));
                    Date timeToday = formatter.parse(jsonToday.getString("eventTime"));
                    // 2. 如果table1、2中都有，且table2时间晚于table1时间1分钟，则type设为late
                    // 3. 其他情况，type设为normal
                    String type = timeToday.getTime() > timePrevDay.getTime() + 24*60*60*1000 + 60*1000 ? "late" : "normal";
                    jsonPrevDay.put("type", type);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return jsonPrevDay.toJSONString();
        }).toStream().to("topic5");


        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);

        streams.start();
    }
}
