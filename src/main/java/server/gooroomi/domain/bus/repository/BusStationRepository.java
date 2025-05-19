package server.gooroomi.domain.bus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.gooroomi.domain.bus.entity.BusStation;

import java.util.Optional;

@Repository
public interface BusStationRepository extends JpaRepository<BusStation, Long> {
    Optional<BusStation> findByUserId(Long userId);
}
