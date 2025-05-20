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
import server.gooroomi.global.handler.response.BaseResponse;
import server.gooroomi.global.handler.response.BaseResponseStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final UserRepository userRepository;

    public BaseResponse<List<BusArrivalResponse>> getBusArrivals(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        String userBusNumber = user.getBusNumber();
        BusStation nearestBusStation = user.getBusStation();

        if (nearestBusStation == null) {
            throw new BaseException(BaseResponseStatus.LOCATION_NOT_UPDATED);
        }

        List<BusArrival> busArrivals = nearestBusStation.getBusArrivals();

        List<BusArrivalResponse> responseList = busArrivals.stream().map(BusConverter::toBusArrivalResponse).toList();

        boolean isUserBusArriving = busArrivals.stream()
                .anyMatch(busArrival -> busArrival.getBusNumber().equals(userBusNumber));

        if (isUserBusArriving) {
            return BaseResponse.success(BaseResponseStatus.USER_BUS_ARRIVING, responseList);
        }

        return BaseResponse.success(responseList);
    }
}
