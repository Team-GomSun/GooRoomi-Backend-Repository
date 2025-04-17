package server.gooroomi.domain.user.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.gooroomi.domain.user.application.UserService;
import server.gooroomi.domain.user.dto.UserBusRequestDto;
import server.gooroomi.global.apiPayload.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    @PostMapping("/bus")
    public ApiResponse<Object> saveUserBusInfo(@RequestBody UserBusRequestDto requestDto) {
        return userService.saveUserBusInfo(requestDto);
    }
}
