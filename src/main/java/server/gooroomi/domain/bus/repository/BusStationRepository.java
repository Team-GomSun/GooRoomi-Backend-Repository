package server.gooroomi.domain.bus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.gooroomi.domain.bus.entity.BusStation;

@Repository
public interface BusStationRepository extends JpaRepository<BusStation, Long> {
    BusStation findByArsId(String arsId);
}
