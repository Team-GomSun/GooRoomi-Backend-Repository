package server.gooroomi.domain.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBusNumberResponse {
    private Long userId;

    @Builder
    public UserBusNumberResponse(Long userId) {
        this.userId = userId;
    }
}
