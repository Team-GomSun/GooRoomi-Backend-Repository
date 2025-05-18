package server.gooroomi.global.dto.WebSocketDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDto {
    private Long userId;
    private double latitude;
    private double longitude;
}
