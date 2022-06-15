package com.example.kafkademo.data;

import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SignalData {
    public static final List<JSONObject> events;
    static {
        events = new ArrayList<>();
        events.add(JSONObject.parseObject("{\"eventId\":1001,\"eventTime\":\"2022-06-12 09:00:00\",\"signalName\":\"signal01\",\"eventSource\":\"kcf\"}"));
        events.add(JSONObject.parseObject("{\"eventId\":1002,\"eventTime\":\"2022-06-13 09:02:00\",\"signalName\":\"signal01\",\"eventSource\":\"kcf\"}"));
        events.add(JSONObject.parseObject("{\"eventId\":1003,\"eventTime\":\"2022-06-13 09:03:00\",\"signalName\":\"signal02\",\"eventSource\":\"kcf\"}"));
        events.add(JSONObject.parseObject("{\"eventId\":1004,\"eventTime\":\"2022-06-13 09:04:00\",\"signalName\":\"signal03\",\"eventSource\":\"kcf\"}"));
        events.add(JSONObject.parseObject("{\"eventId\":1005,\"eventTime\":\"2022-06-14 09:02:00\",\"signalName\":\"signal01\",\"eventSource\":\"kcf\"}"));
        events.add(JSONObject.parseObject("{\"eventId\":1006,\"eventTime\":\"2022-06-14 09:10:01\",\"signalName\":\"signal03\",\"eventSource\":\"kcf\"}"));
    }

    public static List<JSONObject> getEvents() {
        return events;
    }

    public static void main(String[] args) {
        for (JSONObject jsonObject : events) {
            System.out.println(jsonObject.toJSONString());
        }
    }
}
