package server.gooroomi.domain.user.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private Double latitude;  // 위도 (Y)
    private Double longitude; // 경도 (X)
    private String busNumber; // 사용자가 탑승하고자 하는 버스 번호

    @Builder
    public User(String busNumber) {
        this.busNumber = busNumber;
    }

    public void updateLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
