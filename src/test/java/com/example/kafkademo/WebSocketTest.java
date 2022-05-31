package com.example.kafkademo;

import com.example.kafkademo.service.WebSocketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

    @Autowired
    WebSocketService webSocketService;

    @Test
    void webSocketSendMessage(){
        webSocketService.sendMessage("kcf", "TestMessage");
    }
}
