package server.gooroomi.domain.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.gooroomi.domain.user.application.UserService;
import server.gooroomi.domain.user.dto.UserBusNumberRequest;
import server.gooroomi.domain.user.dto.UserBusNumberResponse;
import server.gooroomi.domain.user.dto.UserLocationRequest;
import server.gooroomi.global.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User", description = "User 관련 API")
public class UserRestController {

    private final UserService userService;

    @Operation(summary = "사용자 버스 번호 저장")
    @PostMapping("/bus-number")
    public BaseResponse<UserBusNumberResponse> saveUserBusInfo(@RequestBody UserBusNumberRequest requestDto) {
        return userService.saveUserBusInfo(requestDto);
    }

    @Operation(summary = "사용자 위치 정보 저장", description = """
                        사용자가 근처에 버스 정류소가 존재하지 않는 경우, "code" : 20001 와 함께 응답을 반환.
                        """)
    @PostMapping("/location")
    public BaseResponse<Object> saveUserLocation(@RequestBody UserLocationRequest requestDto) {
        return userService.saveUserLocation(requestDto);
    }
}
