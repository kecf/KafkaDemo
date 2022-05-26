package com.example.kafkademo.controller;


import com.example.kafkademo.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("websocket")
public class WebSocketController {

    @Autowired
    WebSocketService webSocketService;

    @GetMapping("push")
    public void push() {
        webSocketService.sendMessage("kcf","testMessage");
    }
}
