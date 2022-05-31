package com.example.kafkademo.controller;


import com.example.kafkademo.service.OperationService;
import com.example.kafkademo.service.WebSocketService;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("websocket")
public class WebSocketController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);

    @Autowired
    WebSocketService webSocketService;

    @Autowired
    OperationService operationService;

    @PostMapping("/pushMessage")
    public void pushMessage() {
        webSocketService.sendMessage("kcf","testMessage");
        log.info("发送信息成功");
    }

}
