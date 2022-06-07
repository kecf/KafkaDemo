package com.example.kafkademo.data;

import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class EventData {
    public static final List<JSONObject> events;
    static {
        events = new ArrayList<>();
        events.add(JSONObject.parseObject("{\"eventId\":1001,\"eventTime\":\"2022-06-06 09:00:00\",\"eventName\":\"signal1\",\"eventSource\":\"kcf\"}"));
        events.add(JSONObject.parseObject("{\"eventId\":1002,\"eventTime\":\"2022-06-06 09:03:00\",\"eventName\":\"signal2\",\"eventSource\":\"kcf\"}"));
        events.add(JSONObject.parseObject("{\"eventId\":1003,\"eventTime\":\"2022-06-06 09:04:05\",\"eventName\":\"signal3\",\"eventSource\":\"kcf\"}"));
        events.add(JSONObject.parseObject("{\"eventId\":1004,\"eventTime\":\"2022-06-07 09:04:10\",\"eventName\":\"signal1\",\"eventSource\":\"kcf\"}"));
        events.add(JSONObject.parseObject("{\"eventId\":1005,\"eventTime\":\"2022-06-07 10:05:01\",\"eventName\":\"signal3\",\"eventSource\":\"kcf\"}"));
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
