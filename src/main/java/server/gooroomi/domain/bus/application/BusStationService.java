package server.gooroomi.domain.bus.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import server.gooroomi.domain.bus.api.StationInfoApiClient;
import server.gooroomi.domain.bus.dto.BusStationDto;
import server.gooroomi.global.handler.response.BaseException;
import server.gooroomi.global.handler.response.BaseResponseStatus;

/**
 * 버스 정류소 검색 서비스
 * 위치 정보를 기반으로 가장 가까운 정류소 조회
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusStationService {

    private final StationInfoApiClient stationInfoApiClient;

    @Value("${bus.station.search-radius}")
    private int searchRadius;

    /**
     * 위치 정보(위도, 경도)를 기반으로 가장 가까운 정류소 조회
     */
    public BusStationDto findNearestStation(Double latitude, Double longitude) {
        String json = stationInfoApiClient.getNearbyStations(longitude, latitude, searchRadius);

        JSONObject root = new JSONObject(json);
        JSONArray itemList = root.getJSONObject("msgBody").optJSONArray("itemList");

        if (itemList == null || itemList.isEmpty()) {
            throw new BaseException(BaseResponseStatus.STATION_NOT_FOUND);
        }

        // 가장 가까운 정류소 정보 추출
        JSONObject nearest = itemList.getJSONObject(0);
        String arsId = nearest.getString("arsId");
        String stationName = nearest.getString("stationNm");

        log.info("[가장 가까운 정류소] arsId={}, stationName={}", arsId, stationName);

        return new BusStationDto(arsId, stationName);
    }
}
