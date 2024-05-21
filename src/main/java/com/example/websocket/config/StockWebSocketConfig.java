package com.example.websocket.config;

import com.example.websocket.websocket.StockWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket // 웹소켓 활성화 어노테이션
public class StockWebSocketConfig implements WebSocketConfigurer {
    //WebSocketMessageBrokerConfigurer : 기본 WebSocket 핸들러를 설정하고 WebSocket 엔드포인트를 구성
    //낮은 수준의 WebSocket 설정

    private final StockWebSocketHandler stockWebSocketHandler;

    public StockWebSocketConfig(StockWebSocketHandler stockWebSocketHandler) {
        this.stockWebSocketHandler = stockWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        /*webSocketHandler 를 추가*/
        //웹소켓 서버의 엔드포인트는 url:port/stock
        registry.addHandler(stockWebSocketHandler, "/stock").setAllowedOrigins("*"); // endpoint 설정과 CORS 설정(*)
    }
}
