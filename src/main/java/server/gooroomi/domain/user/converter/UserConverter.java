package server.gooroomi.domain.user.converter;

import server.gooroomi.domain.user.dto.UserBusNumberRequest;
import server.gooroomi.domain.user.dto.UserBusNumberResponse;
import server.gooroomi.domain.user.entity.User;

public class UserConverter {
    public static User toUserEntity(UserBusNumberRequest requestDto) {
        return User.builder()
                .busNumber(requestDto.getBusNumber())
                .build();
    }

    public static UserBusNumberResponse toUserBusNumberResponse(User user) {
        return UserBusNumberResponse.builder()
                .userId(user.getId())
                .build();
    }
}
