package com.example.kafkademo.data;

import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class PositionData {
    public static final List<JSONObject> planedPosition;
    public static final List<JSONObject> actualPosition;
    public static final List<JSONObject> heartbeats;

    static {
        planedPosition = new ArrayList<>();
        planedPosition.add(JSONObject.parseObject("{\"eventId\":2001,\"eventTime\":\"2022-06-15 09:00:00\",\"positionType\":\"plan\",\"positionName\":\"position1\"}"));
        planedPosition.add(JSONObject.parseObject("{\"eventId\":2002,\"eventTime\":\"2022-06-15 09:10:00\",\"positionType\":\"plan\",\"positionName\":\"position2\"}"));
        planedPosition.add(JSONObject.parseObject("{\"eventId\":2003,\"eventTime\":\"2022-06-15 09:20:00\",\"positionType\":\"plan\",\"positionName\":\"position3\"}"));
        planedPosition.add(JSONObject.parseObject("{\"eventId\":2004,\"eventTime\":\"2022-06-15 09:30:00\",\"positionType\":\"plan\",\"positionName\":\"position4\"}"));
        planedPosition.add(JSONObject.parseObject("{\"eventId\":2005,\"eventTime\":\"2022-06-15 09:40:00\",\"positionType\":\"plan\",\"positionName\":\"position5\"}"));
        planedPosition.add(JSONObject.parseObject("{\"eventId\":2006,\"eventTime\":\"2022-06-15 09:50:00\",\"positionType\":\"plan\",\"positionName\":\"position6\"}"));

        actualPosition = new ArrayList<>();
        actualPosition.add(JSONObject.parseObject("{\"eventId\":2001,\"eventTime\":\"2022-06-15 09:00:00\",\"positionType\":\"actual\",\"positionName\":\"position1\"}"));// 准时
        actualPosition.add(JSONObject.parseObject("{\"eventId\":2002,\"eventTime\":\"2022-06-15 09:11:00\",\"positionType\":\"actual\",\"positionName\":\"position2\"}"));// 晚4分钟
        actualPosition.add(JSONObject.parseObject("{\"eventId\":2003,\"eventTime\":\"2022-06-15 09:27:00\",\"positionType\":\"actual\",\"positionName\":\"position3\"}"));// 晚7分钟
        actualPosition.add(JSONObject.parseObject("{\"eventId\":2004,\"eventTime\":\"2022-06-15 09:40:00\",\"positionType\":\"actual\",\"positionName\":\"position4\"}"));// 晚10分钟
        actualPosition.add(JSONObject.parseObject("{\"eventId\":2005,\"eventTime\":\"2022-06-15 09:55:00\",\"positionType\":\"actual\",\"positionName\":\"position5\"}"));// 晚15分钟
        // position6缺失

        heartbeats = new ArrayList<>();
        heartbeats.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:00:00\",\"positionType\":\"heartbeat\"}"));
        heartbeats.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:12:00\",\"positionType\":\"heartbeat\"}"));
        heartbeats.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:25:00\",\"positionType\":\"heartbeat\"}"));
        heartbeats.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:30:00\",\"positionType\":\"heartbeat\"}"));
        heartbeats.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:42:00\",\"positionType\":\"heartbeat\"}"));
        heartbeats.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:50:00\",\"positionType\":\"heartbeat\"}"));


    }
}
