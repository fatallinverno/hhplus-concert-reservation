package hhp.concert.reservation.infrastructure.repository;

import hhp.concert.reservation.domain.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
}
