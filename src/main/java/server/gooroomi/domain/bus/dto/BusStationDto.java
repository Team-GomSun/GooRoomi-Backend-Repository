package server.gooroomi.domain.bus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 버스 정류소 정보를 담는 DTO 실시간 데이터
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BusStationDto {
    private String arsId; // 정류소 번호
    private String stationName; // 정류소명
}