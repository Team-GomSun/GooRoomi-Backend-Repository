package server.gooroomi.domain.bus.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.gooroomi.domain.user.entity.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BusArrival extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bus_arrival_id")
    private Long id;

    private String busNumber;  // 버스 번호
    private String arrivalTime;  // 도착 시간 (초)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_station_id")
    private BusStation busStation;

    @Builder
    public BusArrival(String busNumber, String arrivalTime) {
        this.busNumber = busNumber;
        this.arrivalTime = arrivalTime;
    }

    public void assignBusStation(BusStation busStation) {
        this.busStation = busStation;
        busStation.getBusArrivals().add(this);
    }
}
