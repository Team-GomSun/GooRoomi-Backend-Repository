package server.gooroomi.domain.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBusResponseDto {
    private Long userId;

    @Builder
    public UserBusResponseDto(Long userId) {
        this.userId = userId;
    }
}
