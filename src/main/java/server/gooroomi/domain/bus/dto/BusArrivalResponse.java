package server.gooroomi.domain.bus.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusArrivalResponse {
    private String busNumber;

    @Builder
    public BusArrivalResponse(String busNumber) {
        this.busNumber = busNumber;
    }
}
