package server.gooroomi.domain.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.gooroomi.domain.user.converter.UserConverter;
import server.gooroomi.domain.user.dto.UserBusRequestDto;
import server.gooroomi.domain.user.dto.UserLocationRequestDto;
import server.gooroomi.domain.user.entity.User;
import server.gooroomi.domain.user.repository.UserRepository;
import server.gooroomi.global.handler.response.BaseException;
import server.gooroomi.global.handler.response.BaseResponse;
import server.gooroomi.global.handler.response.BaseResponseStatus;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public BaseResponse<Object> saveUserBusInfo(UserBusRequestDto requestDto) {
        User user = UserConverter.toUserEntity(requestDto);
        userRepository.save(user);
        return BaseResponse.success();
    }

    @Transactional
    public BaseResponse<Object> saveUserLocation(UserLocationRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        user.updateLocation(requestDto.getLatitude(), requestDto.getLongitude());
        return BaseResponse.success();
    }
}
