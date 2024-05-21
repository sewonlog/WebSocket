package com.example.websocket.config;

import com.example.websocket.websocket.StockWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket // 웹소켓 활성화 어노테이션
public class StockWebSocketConfig implements WebSocketConfigurer {
    private final StockWebSocketHandler stockWebSocketHandler;

    public StockWebSocketConfig(StockWebSocketHandler stockWebSocketHandler) {
        this.stockWebSocketHandler = stockWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        /*webSocketHandler 를 추가*/
        registry.addHandler(stockWebSocketHandler, "/stock").setAllowedOrigins("*"); // endpoint 설정과 CORS 설정(*)
    }
}
