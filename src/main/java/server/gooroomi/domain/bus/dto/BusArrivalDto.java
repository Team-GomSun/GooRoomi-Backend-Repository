package server.gooroomi.domain.bus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 버스 도착 정보를 담는 DTO 실시간 데이터
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BusArrivalDto {
    private String busNumber; // 버스 번호
    private String arrivalTime; // 도착 예정 시간 (초)
}