package server.gooroomi.domain.bus.application;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import server.gooroomi.domain.bus.api.BusInfoApiClient;
import server.gooroomi.domain.bus.converter.BusConverter;
import server.gooroomi.domain.bus.entity.BusArrival;
import server.gooroomi.domain.bus.entity.BusStation;
import server.gooroomi.domain.bus.repository.BusArrivalRepository;
import server.gooroomi.domain.bus.repository.BusStationRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class BusArrivalInfoService {

    private final BusInfoApiClient busInfoApiClient;
    private final BusArrivalRepository busArrivalRepository;
    private final BusStationRepository busStationRepository;

    public void saveBusArrivalInfo(String arsId) {
        String json = busInfoApiClient.getBusArrivals(arsId);
        BusStation busStation = busStationRepository.findByArsId(arsId);
        busStation.getBusArrivals().clear();

        JSONObject root = new JSONObject(json);
        JSONArray itemList = root.getJSONObject("msgBody").optJSONArray("itemList");

        List<BusArrival> busArrivals = IntStream.range(0, itemList.length())
                .mapToObj(i -> {
                    JSONObject item = itemList.getJSONObject(i);
                    String busNumber = item.getString("rtNm");
                    String arrivalTime = item.getString("traTime1");
                    String arrmsg1 = item.getString("arrmsg1");
                    int arrivalInSeconds = Integer.parseInt(arrivalTime);

                    if ("운행종료".equals(arrmsg1) || "출발대기".equals(arrmsg1) || arrivalInSeconds > 90) {
                        return null;
                    }

                    BusArrival busArrival = BusConverter.toBusArrival(busNumber, arrivalTime);
                    busArrival.assignBusStation(busStation);
                    return busArrival;
                })
                .filter(Objects::nonNull)
                .toList();

        busArrivalRepository.saveAll(busArrivals);
    }
}
