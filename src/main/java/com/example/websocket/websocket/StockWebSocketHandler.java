package com.example.websocket.websocket;

import com.example.websocket.domain.StockPriceResponseDto;
import com.example.websocket.service.StockWebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class StockWebSocketHandler extends TextWebSocketHandler {
    private final StockWebSocketService stockWebSocketService;
    private final Map<WebSocketSession, String> sessionStockCodeMap = new ConcurrentHashMap<>();

    public StockWebSocketHandler(StockWebSocketService stockWebSocketService){
        this.stockWebSocketService = stockWebSocketService;
    }
    Map<String, WebSocketSession> sessionMap = new HashMap<>(); /*웹소켓 세션을 담아둘 맵*/

    /* 클라이언트로부터 메시지 수신시 동작 */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String stockCode = message.getPayload(); /*stockCode <- 클라이언트에서 입력한 message*/
        log.info("===============Message=================");
        log.info("Received stockCode : {}", stockCode);
        log.info("===============Message=================");
        synchronized (sessionMap) {
            sessionStockCodeMap.put(session, stockCode);
        }
    }

    /* 클라이언트가 소켓 연결시 동작 */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Web Socket Connected");
        log.info("session id : {}",session.getId());
        super.afterConnectionEstablished(session);
        synchronized (sessionMap) { // 여러 클라이언트의 동시 접근하여 Map의 SessionID가 변경되는 것을 막기위해
            sessionMap.put(session.getId(), session);
        }
        System.out.println("sessionMap :" + sessionMap.toString());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionId",session.getId());

        session.sendMessage(new TextMessage(jsonObject.toString()));
    }

    /* 클라이언트가 소켓 종료시 동작 */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Web Socket DisConnected");
        log.info("session id : {}", session.getId());
        synchronized (sessionMap) { // 여러 클라이언트의 동시 접근하여 Map의 SessionID가 변경되는 것을 막기위해
            sessionMap.remove(session.getId());
        }
        super.afterConnectionClosed(session,status); /*실제로 closed*/
    }

    @Scheduled(fixedRate = 5000)
    public void sendStockCode() throws JSONException, IOException {
        synchronized (sessionMap){
            for (WebSocketSession session : sessionMap.values()){
                String stockCode = sessionStockCodeMap.get(session);
                if(stockCode!=null) {
                    try { // 주식 데이터를 가져오는 로직이 길어 service 단에 설계
                        StockPriceResponseDto stockPriceResponseDto = stockWebSocketService.getStock(stockCode);
                        if (stockPriceResponseDto != null) {
                            String response = new ObjectMapper().writeValueAsString(stockPriceResponseDto);
                            log.info("Sending stock data : {}", response);
                            try {
                                session.sendMessage(new TextMessage(response));
                                // Message 보내기
                            }catch (IllegalStateException ex){
                                log.warn("Failed to send message, ignoring: {}",ex.getMessage());
                            }
                        } else {
                            log.warn("No stock data found for stockCode : {}", stockCode);
                        }
                    } catch (Exception e) {
                        log.error("Error while sending stock data : {}", e.getMessage());
                    }
                }
            }
        }
    }



}
