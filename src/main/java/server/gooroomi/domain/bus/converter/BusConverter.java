package server.gooroomi.domain.bus.converter;

import server.gooroomi.domain.bus.dto.BusArrivalResponse;
import server.gooroomi.domain.bus.entity.BusArrival;
import server.gooroomi.domain.bus.entity.BusStation;

public class BusConverter {
    public static BusStation toBusStation(String arsId, String stationNm) {
        return BusStation.builder()
                .arsId(arsId)
                .stationName(stationNm)
                .build();
    }

    public static BusArrival toBusArrival(String busNumber, String arrivalTime) {
        return BusArrival.builder()
                .busNumber(busNumber)
                .arrivalTime(arrivalTime)
                .build();
    }

    public static BusArrivalResponse toBusArrivalResponse(BusArrival busArrival) {
        return BusArrivalResponse.builder()
                .busNumber(busArrival.getBusNumber())
                .build();
    }
}
