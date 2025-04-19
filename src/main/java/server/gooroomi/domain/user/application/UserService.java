package server.gooroomi.domain.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.gooroomi.domain.user.converter.UserConverter;
import server.gooroomi.domain.user.dto.UserLocationRequestDto;
import server.gooroomi.domain.user.entity.User;
import server.gooroomi.domain.user.repository.UserRepository;
import server.gooroomi.domain.user.dto.UserBusRequestDto;
import server.gooroomi.global.apiPayload.ApiResponse;
import server.gooroomi.global.apiPayload.code.status.ErrorStatus;
import server.gooroomi.global.apiPayload.code.status.SuccessStatus;
import server.gooroomi.global.apiPayload.exception.GeneralException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public ApiResponse<Object> saveUserBusInfo(UserBusRequestDto requestDto) {
        User user = UserConverter.toUserEntity(requestDto);
        userRepository.save(user);
        return ApiResponse.onSuccess(SuccessStatus._OK, user);
    }

    @Transactional
    public ApiResponse<Object> saveUserLocation(UserLocationRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_FOUND));

        user.updateLocation(requestDto.getLatitude(), requestDto.getLongitude());
        return ApiResponse.onSuccess(SuccessStatus._OK, user);
    }
}
