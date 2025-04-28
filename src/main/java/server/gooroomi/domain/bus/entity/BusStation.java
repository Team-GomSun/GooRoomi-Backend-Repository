package server.gooroomi.domain.bus.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.gooroomi.domain.user.entity.BaseTimeEntity;
import server.gooroomi.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BusStation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bus_station_id")
    private Long id;

    private String arsId; // 정류소 번호
    private String stationName; // 정류소명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "busStation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusArrival> busArrivals = new ArrayList<>();

    @Builder
    public BusStation(String arsId, String stationName, User user) {
        this.arsId = arsId;
        this.stationName = stationName;
        this.user = user;
    }
}
