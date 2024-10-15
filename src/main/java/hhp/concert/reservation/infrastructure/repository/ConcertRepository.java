package hhp.concert.reservation.infrastructure.repository;


import hhp.concert.reservation.domain.entity.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<ConcertEntity, Long> {
}
