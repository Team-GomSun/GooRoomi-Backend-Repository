package server.gooroomi.domain.user.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.gooroomi.domain.user.application.UserService;
import server.gooroomi.domain.user.dto.UserBusRequestDto;
import server.gooroomi.domain.user.dto.UserBusResponseDto;
import server.gooroomi.domain.user.dto.UserLocationRequestDto;
import server.gooroomi.global.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    @PostMapping("/bus")
    public BaseResponse<UserBusResponseDto> saveUserBusInfo(@RequestBody UserBusRequestDto requestDto) {
        return userService.saveUserBusInfo(requestDto);
    }

    @PostMapping("/location")
    public BaseResponse<Object> saveUserLocation(@RequestBody UserLocationRequestDto requestDto) {
        return userService.saveUserLocation(requestDto);
    }
}
