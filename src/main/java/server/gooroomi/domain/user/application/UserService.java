package server.gooroomi.domain.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.gooroomi.domain.bus.application.BusStationAssignService;
import server.gooroomi.domain.user.converter.UserConverter;
import server.gooroomi.domain.user.dto.UserBusNumberRequest;
import server.gooroomi.domain.user.dto.UserBusNumberResponse;
import server.gooroomi.domain.user.dto.UserLocationRequest;
import server.gooroomi.domain.user.entity.User;
import server.gooroomi.domain.user.repository.UserRepository;
import server.gooroomi.global.handler.response.BaseException;
import server.gooroomi.global.handler.response.BaseResponse;
import server.gooroomi.global.handler.response.BaseResponseStatus;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BusStationAssignService busStationAssignService;

    @Transactional
    public BaseResponse<UserBusNumberResponse> saveUserBusInfo(UserBusNumberRequest requestDto) {
        User user = UserConverter.toUserEntity(requestDto);
        userRepository.save(user);
        UserBusNumberResponse response = UserConverter.toUserBusNumberResponse(user);
        return BaseResponse.success(response);
    }

    @Transactional
    public BaseResponse<Object> saveUserLocation(UserLocationRequest requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
        user.updateLocation(requestDto.getLatitude(), requestDto.getLongitude());
        busStationAssignService.saveBusStation(user);
        return BaseResponse.success();
    }
}
