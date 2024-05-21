package com.example.websocket.websocket;

import com.example.websocket.domain.Message;
import com.jayway.jsonpath.internal.Utils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 웹소켓 연결이 된 후에 호출
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var sessionId = session.getId();
        sessions.put(sessionId, session);

        Message message = Message.builder().sender(sessionId).receiver("all").build();
        message.newConnect();

        sessions.values().forEach(s -> {
            try {
                if(!s.getId().equals(sessionId)) {
                    s.sendMessage(new TextMessage("connect"));
                }
            } catch (Exception e) {

            }
        });
    }

    // 웹소켓 메세지가 도착하면 호출
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

    }

    // 웹소켓 메세지 오류 처리
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    // 웹소켓 연결이 해제된 후에 호출
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {

    }



}
