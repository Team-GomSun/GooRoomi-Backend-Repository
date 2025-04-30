package server.gooroomi.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLocationRequest {
    private Long userId;
    private Double latitude;
    private Double longitude;
}
