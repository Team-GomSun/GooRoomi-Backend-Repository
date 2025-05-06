package server.gooroomi.domain.bus.application;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import server.gooroomi.domain.bus.api.StationInfoApiClient;
import server.gooroomi.domain.bus.converter.BusConverter;
import server.gooroomi.domain.bus.entity.BusStation;
import server.gooroomi.domain.bus.repository.BusStationRepository;
import server.gooroomi.domain.user.entity.User;
import server.gooroomi.global.handler.response.BaseException;
import server.gooroomi.global.handler.response.BaseResponseStatus;

@Service
@RequiredArgsConstructor
public class BusStationAssignService {

    private final StationInfoApiClient stationInfoApiClient;
    private final BusStationRepository busStationRepository;
    private final BusArrivalInfoService busArrivalInfoService;

    public void saveBusStation(User user) {
        String json = stationInfoApiClient.getNearbyStations(user.getLongitude(), user.getLatitude(), 100);

        JSONObject root = new JSONObject(json);
        JSONArray itemList = root.getJSONObject("msgBody").optJSONArray("itemList");

        if(itemList == null || itemList.isEmpty()){
            throw new BaseException(BaseResponseStatus.STATION_NOT_FOUND);
        }

        JSONObject nearest = itemList.getJSONObject(0);
        String arsId = nearest.getString("arsId");
        String stationNm = nearest.getString("stationNm");

        BusStation busStation = user.getBusStation();

        if (busStation == null) {
            busStation = BusConverter.toBusStation(arsId, stationNm);
            busStation.assignUserBusStation(user);
            busStationRepository.save(busStation);
        } else if (!busStation.getArsId().equals(arsId)) {
            busStation.updateBusStationInfo(arsId, stationNm);
        }

        busArrivalInfoService.saveBusArrivalInfo(arsId, busStation);
    }
}
