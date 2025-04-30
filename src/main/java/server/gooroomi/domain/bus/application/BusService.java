package server.gooroomi.domain.bus.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.gooroomi.domain.bus.converter.BusConverter;
import server.gooroomi.domain.bus.dto.BusArrivalResponse;
import server.gooroomi.domain.bus.entity.BusArrival;
import server.gooroomi.domain.bus.entity.BusStation;
import server.gooroomi.domain.user.entity.User;
import server.gooroomi.domain.user.repository.UserRepository;
import server.gooroomi.global.handler.response.BaseException;
import server.gooroomi.global.handler.response.BaseResponseStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final UserRepository userRepository;

    public List<BusArrivalResponse> getArrivingBuses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        BusStation nearestBusStation = user.getBusStation();
        List<BusArrival> busArrivals = nearestBusStation.getBusArrivals();

        return busArrivals.stream()
                .map(BusConverter::toBusArrivalResponse)
                .toList();
    }
}
