package server.gooroomi.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import server.gooroomi.domain.bus.api.BusInfoApiClient;
import server.gooroomi.domain.bus.api.StationInfoApiClient;
import server.gooroomi.domain.bus.converter.BusConverter;
import server.gooroomi.domain.bus.entity.BusArrival;
import server.gooroomi.domain.bus.entity.BusStation;
import server.gooroomi.domain.bus.repository.BusStationRepository;
import server.gooroomi.domain.user.entity.User;
import server.gooroomi.domain.user.repository.UserRepository;
import server.gooroomi.global.dto.WebSocketDto.LocationDto;
import server.gooroomi.global.handler.response.BaseException;
import server.gooroomi.global.handler.response.BaseResponseStatus;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
@Slf4j
public class LocationWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final StationInfoApiClient stationInfoApiClient;
    private final BusInfoApiClient busInfoApiClient;
    private final BusStationRepository busStationRepository;

    // 연결된 WebSocket 세션을 저장하는 Map
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Value("${bus.station.search-radius}")
    private int searchRadius;

    // 클라이언트와 WebSocket 연결이 수립되었을 때 호출됨
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.info("WebSocket 연결됨: {}", session.getId());
    }

    // 클라이언트로부터 메시지를 수신했을 때 호출됨 (위/경도 기반 처리)
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // JSON 메시지를 DTO로 변환
        LocationDto latLng = objectMapper.readValue(message.getPayload(), LocationDto.class);
        log.info("받은 위치: 위도 = {}, 경도 = {}", latLng.getLatitude(), latLng.getLongitude());

        // 사용자 조회
        User user = userRepository.findById(latLng.getUserId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        // 사용자 위치 업데이트
        user.updateLocation(latLng.getLatitude(), latLng.getLongitude());

        // 사용자 위치 기반으로 가장 가까운 정류장 조회
        String stationJson = stationInfoApiClient.getNearbyStations(latLng.getLongitude(), latLng.getLatitude(), searchRadius);
        JSONObject stationRoot = new JSONObject(stationJson);
        JSONArray stationList = stationRoot.getJSONObject("msgBody").optJSONArray("itemList");

        if (stationList == null || stationList.isEmpty()) {
            session.sendMessage(new TextMessage("가까운 정류장이 없습니다."));
            return;
        }

        // 가장 가까운 정류장의 arsId 추출
        JSONObject nearestStation = stationList.getJSONObject(0);
        String arsId = nearestStation.getString("arsId");
        String stationNm = nearestStation.getString("stationNm");

        // 해당 arsId로 도착 예정 버스 정보 조회
        String arrivalJson = busInfoApiClient.getBusArrivals(arsId);
        JSONObject arrivalRoot = new JSONObject(arrivalJson);
        JSONArray arrivalList = arrivalRoot.getJSONObject("msgBody").optJSONArray("itemList");

        // 정류장 생성 후 사용자에 할당
        BusStation busStation = BusConverter.toBusStation(arsId, stationNm);
        busStation.assignUserBusStation(user);

        // 기존 도착 정보 초기화
        busStation.getBusArrivals().clear();

        // 도착 예정 버스 필터칭 및 엔티티 생성
        List<BusArrival> busArrivals = IntStream.range(0, arrivalList.length())
                .mapToObj(i -> {
                    JSONObject item = arrivalList.getJSONObject(i);
                    String busNumber = item.getString("rtNm");
                    String arrivalTime = item.getString("traTime1");
                    String arrmsg1 = item.getString("arrmsg1");
                    int seconds = Integer.parseInt(arrivalTime);
                    if ("운행종료".equals(arrmsg1) || "출발대기".equals(arrmsg1) || seconds > 120) return null;
                    BusArrival busArrival = BusConverter.toBusArrival(busNumber, arrivalTime);
                    busArrival.assignBusStation(busStation);
                    return busArrival;
                })
                .filter(Objects::nonNull)
                .toList();

        // 버스정류장에 곧 도착 버스 연결 후 저장
        busStation.getBusArrivals().addAll(busArrivals);
        busStationRepository.save(busStation);
    }

    // WebSocket 연결이 종료되었을 때 호출됨
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        log.info("WebSocket 연결 종료: {}", session.getId());
    }
}
