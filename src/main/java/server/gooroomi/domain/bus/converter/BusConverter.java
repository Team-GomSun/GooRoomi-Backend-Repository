package server.gooroomi.domain.bus.converter;

import server.gooroomi.domain.bus.entity.BusStation;
import server.gooroomi.domain.user.entity.User;

public class BusConverter {
    public static BusStation toBusStation(String arsId, String stationNm, User user) {
        return BusStation.builder()
                .arsId(arsId)
                .stationName(stationNm)
                .user(user)
                .build();
    }
}
