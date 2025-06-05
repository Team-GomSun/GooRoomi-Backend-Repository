package server.gooroomi.domain.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 사용자 버스 번호 저장
     */
    @Transactional
    public BaseResponse<UserBusNumberResponse> saveUserBusInfo(UserBusNumberRequest requestDto) {
        User user = UserConverter.toUserEntity(requestDto);
        userRepository.save(user);
        UserBusNumberResponse response = UserConverter.toUserBusNumberResponse(user);
        return BaseResponse.success(response);
    }

    /**
     * 사용자 위치 정보 저장
     */
    @Transactional
    public BaseResponse<Object> saveUserLocation(UserLocationRequest requestDto) {
        User user = getUserById(requestDto.getUserId());
        user.updateLocation(requestDto.getLatitude(), requestDto.getLongitude());
        return BaseResponse.success();
    }

    /**
     * 사용자 조회
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));
    }
}
