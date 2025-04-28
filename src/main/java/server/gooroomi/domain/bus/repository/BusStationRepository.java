package server.gooroomi.domain.bus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.gooroomi.domain.bus.entity.BusStation;
import server.gooroomi.domain.user.entity.User;

@Repository
public interface BusStationRepository extends JpaRepository<BusStation, Long> {
    BusStation findByArsId(String arsId);
    BusStation findByUser(User user);
}
