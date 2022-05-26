package com.example.kafkademo.model;

import lombok.Data;

import javax.websocket.Session;

@Data
public class WebSocketClient {
    private Session session;
    private String uri;
}
