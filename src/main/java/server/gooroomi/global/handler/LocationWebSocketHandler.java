package server.gooroomi.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import server.gooroomi.global.dto.WebSocketDto.LocationDto;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LocationWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.info("WebSocket 연결됨: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        LocationDto latLng = objectMapper.readValue(message.getPayload(), LocationDto.class);
        log.info("받은 위치: 위도 = {}, 경도 = {}", latLng.getLatitude(), latLng.getLongitude());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        log.info("WebSocket 연결 종료: {}", session.getId());
    }
}
