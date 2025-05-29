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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusService {

    private final UserRepository userRepository;

    /**
     * 사용자 ID를 기반으로 버스 도착 정보 조회
     */
    public BaseResponse<List<BusArrivalResponse>> getBusArrivals(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        BusStation busStation = getBusStationFromUser(user);
        String userBusNumber = user.getBusNumber();

        // 버스 도착 정보 조회 및 변환
        List<BusArrival> busArrivals = busStation.getBusArrivals();
        List<BusArrivalResponse> responseList = busArrivals.stream().map(BusConverter::toBusArrivalResponse)
                .collect(Collectors.toList());

        // 사용자 버스 도착 여부에 따른 응답
        return getBusArrivalResponse(userBusNumber, busArrivals, responseList);
    }

    /**
     * 사용자 버스 정류소 정보 조회
     */
    private BusStation getBusStationFromUser(User user) {
        BusStation busStation = user.getBusStation();
        if (busStation == null) {
            throw new BaseException(BaseResponseStatus.LOCATION_NOT_UPDATED);
        }
        return busStation;
    }

    /**
     * 사용자가 등록한 버스의 도착 여부에 따라 다른 응답
     */
    private BaseResponse<List<BusArrivalResponse>> getBusArrivalResponse(String userBusNumber,
            List<BusArrival> busArrivals, List<BusArrivalResponse> responseList) {

        // 사용자가 등록한 버스가 도착 예정 버스 목록에 있는지 확인
        boolean isUserBusArriving = busArrivals.stream()
                .anyMatch(busArrival -> busArrival.getBusNumber().equals(userBusNumber));

        // 사용자의 버스가 도착 예정이 아닌 경우
        if (!isUserBusArriving) {
            return BaseResponse.success(responseList);
        }

        // 도착 예정인 버스가 사용자의 버스 1대만 있는 경우 (code: 20002)
        if (isSingleUserBusArriving(busArrivals, userBusNumber)) {
            return BaseResponse.success(BaseResponseStatus.USER_BUS_ARRIVING, responseList);
        }

        // 도착 예정인 버스가 여러 대이고, 그 중에 사용자의 버스가 포함된 경우 (code: 20003)
        return BaseResponse.success(BaseResponseStatus.MULTIPLE_BUSES_ARRIVING, responseList);
    }

    /**
     * 도착 예정인 버스가 사용자의 버스 1대만 있는지 확인
     */
    private boolean isSingleUserBusArriving(List<BusArrival> busArrivals, String userBusNumber) {
        return busArrivals.size() == 1 && busArrivals.get(0).getBusNumber().equals(userBusNumber);
    }
}
