package com.example.kafkademo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.kafkademo.model.WebSocketClient;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.kstream.KTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/websocket/{userName}")
@Component
@Data
public class WebSocketService {
    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);
    //静态变量，用来记录当前在线连接数
    private static int onlineCount = 0;
    //用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String, WebSocketClient> webSocketMap = new ConcurrentHashMap<>();
    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    // 接收userName
    private String userName="";

    @OnOpen
    public void onOpen(Session session, @PathParam("userName") String userName) {
        if(!webSocketMap.containsKey(userName))
        {
            addOnlineCount(); // 在线数 +1
        }

        this.session = session;
        this.userName = userName;
        WebSocketClient client = new WebSocketClient();
        client.setSession(session);
        client.setUri(session.getRequestURI().toString());
        webSocketMap.put(userName, client);

        log.info("-----------------------------------------------");
        log.info("用户连接:"+userName+",当前在线人数为:" + getOnlineCount());

        try {
            sendMessage("来自后台的反馈：连接成功");
        } catch (IOException e) {
            log.error("用户:"+userName+",网络异常!!!!!!");
        }
    }

    @OnClose
    public void onClose() {
        if(webSocketMap.containsKey(userName)){
            webSocketMap.remove(userName);
            if(webSocketMap.size()>0)
            {
                subOnlineCount();
            }
        }
        log.info("----------------------------------------------------------------------------");
        log.info(userName+"用户退出,当前在线人数为:" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message) throws IOException {
        log.info("收到用户消息:" + userName + ",报文:" + message);
        sendMessage("来自后台的反馈：已接收前端数据");
    }

    @OnError
    public void onError(Throwable error) {
        log.error("用户错误:" + this.userName + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    private void sendMessage(String message) throws IOException {
        synchronized (this) {
            this.session.getBasicRemote().sendText(message);
        }
    }

    public void sendMessage(String userName, String message) {
        try {
            WebSocketClient webSocketClient = webSocketMap.get(userName);
            if(webSocketClient!=null){
                webSocketClient.getSession().getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public <V> void sendJson(String userName, String key, V value) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, value);
        String jsonString = JSON.toJSONString(jsonObject);
        try {
            WebSocketClient webSocketClient = webSocketMap.get(userName);
            if(webSocketClient!=null){
                webSocketClient.getSession().getBasicRemote().sendText(jsonString);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public synchronized int getOnlineCount() {
        return onlineCount;
    }

    private synchronized void addOnlineCount() {
        onlineCount++;
    }

    private synchronized void subOnlineCount() {
        onlineCount--;
    }
}
