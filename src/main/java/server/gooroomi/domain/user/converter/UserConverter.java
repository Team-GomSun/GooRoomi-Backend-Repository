package server.gooroomi.domain.user.converter;

import server.gooroomi.domain.user.dto.UserBusResponseDto;
import server.gooroomi.domain.user.entity.User;
import server.gooroomi.domain.user.dto.UserBusRequestDto;

public class UserConverter {
    public static User toUserEntity(UserBusRequestDto dto) {
        return User.builder()
                .busNumber(dto.getBusNumber())
                .build();
    }

    public static UserBusResponseDto toUserBusResponseDto(User user) {
        return UserBusResponseDto.builder()
                .userId(user.getId())
                .build();
    }
}
