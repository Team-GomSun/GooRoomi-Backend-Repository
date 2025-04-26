package server.gooroomi.domain.bus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.gooroomi.domain.bus.entity.BusArrival;

@Repository
public interface BusArrivalRepository extends JpaRepository<BusArrival, Long> {
}
