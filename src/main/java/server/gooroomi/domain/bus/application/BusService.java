package server.gooroomi.domain.bus.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import server.gooroomi.domain.bus.dto.BusArrivalDto;
import server.gooroomi.domain.bus.dto.BusArrivalResponse;
import server.gooroomi.domain.bus.dto.BusStationDto;
import server.gooroomi.domain.user.application.UserService;
import server.gooroomi.domain.user.entity.User;
import server.gooroomi.global.handler.response.BaseException;
import server.gooroomi.global.handler.response.BaseResponse;
import server.gooroomi.global.handler.response.BaseResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 버스 서비스 사용자 위치 기반으로 버스 도착 정보를 조회
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusService {

    private final BusStationService busStationService;
    private final BusArrivalService busArrivalService;
    private final BusAlertService busAlertService;
    private final UserService userService;

    /**
     * 사용자 ID를 기반으로 버스 도착 정보 조회
     */
    public BaseResponse<List<BusArrivalResponse>> getBusArrivals(Long userId) {
        // 사용자 조회
        User user = userService.getUserById(userId);

        // 위치 정보 확인
        if (user.getLatitude() == null || user.getLongitude() == null) {
            throw new BaseException(BaseResponseStatus.LOCATION_NOT_REGISTERED);
        }

        // 가장 가까운 정류장 조회
        BusStationDto stationDto = busStationService.findNearestStation(user.getLatitude(), user.getLongitude());

        // 도착 예정 버스 목록 조회
        List<BusArrivalDto> busArrivals = busArrivalService.getBusArrivals(stationDto.getArsId());

        // 알림 서비스에 버스 도착 정보 전달
        busAlertService.notifyUserIfBusArriving(userId, busArrivals);

        // 응답 생성
        List<BusArrivalResponse> responseList = busArrivals.stream()
                .map(dto -> new BusArrivalResponse(dto.getBusNumber())).collect(Collectors.toList());

        // 사용자 버스 도착 여부에 따른 응답
        return getBusArrivalResponse(user.getBusNumber(), busArrivals, responseList);
    }

    /**
     * 사용자가 등록한 버스의 도착 여부에 따라 다른 응답
     */
    private BaseResponse<List<BusArrivalResponse>> getBusArrivalResponse(String userBusNumber,
            List<BusArrivalDto> busArrivals, List<BusArrivalResponse> responseList) {

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
    private boolean isSingleUserBusArriving(List<BusArrivalDto> busArrivals, String userBusNumber) {
        return busArrivals.size() == 1 && busArrivals.get(0).getBusNumber().equals(userBusNumber);
    }
}
