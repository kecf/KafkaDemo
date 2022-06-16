package com.example.kafkademo.data;

import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class PositionData {
    public static final List<JSONObject> planedPosition;
    public static final List<JSONObject> actualPosition;

    static {
        planedPosition = new ArrayList<>();
        planedPosition.add(JSONObject.parseObject("{\"eventId\":2001,\"eventTime\":\"2022-06-15 09:00:00\",\"positionType\":\"plan\",\"positionName\":\"position01\"}"));
        planedPosition.add(JSONObject.parseObject("{\"eventId\":2002,\"eventTime\":\"2022-06-15 09:10:00\",\"positionType\":\"plan\",\"positionName\":\"position02\"}"));
        planedPosition.add(JSONObject.parseObject("{\"eventId\":2003,\"eventTime\":\"2022-06-15 09:20:00\",\"positionType\":\"plan\",\"positionName\":\"position03\"}"));
        planedPosition.add(JSONObject.parseObject("{\"eventId\":2004,\"eventTime\":\"2022-06-15 09:30:00\",\"positionType\":\"plan\",\"positionName\":\"position04\"}"));
        planedPosition.add(JSONObject.parseObject("{\"eventId\":2005,\"eventTime\":\"2022-06-15 09:40:00\",\"positionType\":\"plan\",\"positionName\":\"position05\"}"));

        actualPosition = new ArrayList<>();
        actualPosition.add(JSONObject.parseObject("{\"eventId\":2001,\"eventTime\":\"2022-06-15 09:00:00\",\"positionType\":\"actual\",\"positionName\":\"position01\"}"));// 准时
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:01:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
        actualPosition.add(JSONObject.parseObject("{\"eventId\":2002,\"eventTime\":\"2022-06-15 09:11:00\",\"positionType\":\"actual\",\"positionName\":\"position02\"}"));// 晚1分钟
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:11:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:21:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:24:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
        actualPosition.add(JSONObject.parseObject("{\"eventId\":2003,\"eventTime\":\"2022-06-15 09:25:00\",\"positionType\":\"actual\",\"positionName\":\"position03\"}"));// 晚5分钟
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:26:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:31:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:34:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:37:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
        actualPosition.add(JSONObject.parseObject("{\"eventId\":2004,\"eventTime\":\"2022-06-15 09:38:00\",\"positionType\":\"actual\",\"positionName\":\"position04\"}"));// 晚8分钟
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:39:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:41:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:44:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:48:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
        actualPosition.add(JSONObject.parseObject("{\"eventId\":2005,\"eventTime\":\"2022-06-15 09:50:00\",\"positionType\":\"actual\",\"positionName\":\"position05\"}"));// 晚10分钟
        actualPosition.add(JSONObject.parseObject("{\"eventTime\":\"2022-06-15 09:51:00\",\"positionName\":\"heartbeat\",\"type\":\"heartbeat\"}"));
    }
}
