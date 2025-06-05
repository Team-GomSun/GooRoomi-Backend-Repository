package server.gooroomi.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import server.gooroomi.domain.bus.application.BusAlertService;
import server.gooroomi.domain.bus.application.BusArrivalService;
import server.gooroomi.domain.bus.application.BusStationService;
import server.gooroomi.domain.bus.dto.BusArrivalDto;
import server.gooroomi.domain.bus.dto.BusStationDto;
import server.gooroomi.domain.user.application.UserService;
import server.gooroomi.domain.user.entity.User;
import server.gooroomi.global.dto.WebSocketDto.LocationDto;
import server.gooroomi.global.handler.response.BaseException;
import server.gooroomi.global.handler.response.BaseResponseStatus;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 위치 정보 WebSocket 핸들러 클라이언트로부터 위치 정보를 수신하고 버스 도착 정보를 처리하는 역할 담당
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class LocationWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;
    private final BusStationService busStationService;
    private final BusArrivalService busArrivalService;
    private final BusAlertService busAlertService;

    // 연결된 WebSocket 세션을 저장하는 Map
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 클라이언트와 WebSocket 연결이 수립되었을 때 호출됨
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.info("WebSocket 연결됨: {}", session.getId());
    }

    // 클라이언트로부터 메시지를 수신했을 때 호출됨 (위/경도 기반 처리)
    @Override
    @Transactional
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // JSON 메시지를 DTO로 변환
        LocationDto locationDto = objectMapper.readValue(message.getPayload(), LocationDto.class);
        log.info("받은 위치: 위도 = {}, 경도 = {}", locationDto.getLatitude(), locationDto.getLongitude());

        try {
            // 사용자 조회 및 위치 업데이트
            User user = userService.getUserById(locationDto.getUserId());
            user.updateLocation(locationDto.getLatitude(), locationDto.getLongitude());

            // 가장 가까운 정류장 조회
            BusStationDto stationDto = busStationService.findNearestStation(locationDto.getLatitude(),
                    locationDto.getLongitude());

            // 도착 예정 버스 목록 조회
            List<BusArrivalDto> busArrivals = busArrivalService.getBusArrivals(stationDto.getArsId());

            // 사용자에게 버스 도착 알림 전송
            busAlertService.notifyUserIfBusArriving(user.getId(), busArrivals);

            // 정류장 정보를 클라이언트에게 응답
            session.sendMessage(
                    new TextMessage("정류장 정보: " + stationDto.getStationName() + " (" + stationDto.getArsId() + ")"));

        } catch (BaseException e) {
            if (e.getStatus() == BaseResponseStatus.STATION_NOT_FOUND) {
                session.sendMessage(new TextMessage("가까운 정류장이 없습니다."));
            } else {
                throw e;
            }
        }
    }

    // WebSocket 연결이 종료되었을 때 호출됨
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        log.info("WebSocket 연결 종료: {}", session.getId());
    }
}
