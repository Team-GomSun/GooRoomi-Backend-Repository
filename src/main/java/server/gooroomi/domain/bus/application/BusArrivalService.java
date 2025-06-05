package server.gooroomi.domain.bus.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import server.gooroomi.domain.bus.api.BusInfoApiClient;
import server.gooroomi.domain.bus.dto.BusArrivalDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 버스 도착 정보 조회 서비스 정류소 ID(arsId)를 기반으로 도착 예정 버스 정보 조회
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusArrivalService {

    private final BusInfoApiClient busInfoApiClient;

    /**
     * 정류소 ID(arsId)로 도착 예정 버스 목록 조회
     */
    public List<BusArrivalDto> getBusArrivals(String arsId) {
        String json = busInfoApiClient.getBusArrivals(arsId);
        JSONObject root = new JSONObject(json);
        JSONArray itemList = root.getJSONObject("msgBody").optJSONArray("itemList");

        if (itemList == null) {
            log.info("정류장 ID {}에 도착 예정인 버스가 없습니다.", arsId);
            return new ArrayList<>();
        }

        // 도착 예정 버스 목록 중 조건에 맞는 버스만 필터링하여 DTO로 변환
        List<BusArrivalDto> busArrivals = IntStream.range(0, itemList.length()).mapToObj(i -> {
            JSONObject item = itemList.getJSONObject(i);
            String busNumber = item.getString("rtNm");
            String arrivalTime = item.getString("traTime1");
            String arrmsg1 = item.getString("arrmsg1");
            int arrivalInSeconds = Integer.parseInt(arrivalTime);

            // 운행종료, 출발대기, 90초 이상 남은 버스는 제외
            if ("운행종료".equals(arrmsg1) || "출발대기".equals(arrmsg1) || arrivalInSeconds > 90) {
                return null;
            }

            return new BusArrivalDto(busNumber, arrivalTime);
        }).filter(Objects::nonNull).toList();

        // 도착 예정 버스 목록 로깅
        if (busArrivals.isEmpty()) {
            log.info("정류장 ID {}에 90초 이내 도착 예정인 버스가 없습니다.", arsId);
        } else {
            log.info("[정류장 ID {}에 도착 예정인 버스 목록] {}", arsId,
                    busArrivals.stream().map(bus -> bus.getBusNumber() + "(" + bus.getArrivalTime() + "초)")
                            .collect(Collectors.joining(", ")));
        }

        return busArrivals;
    }
}