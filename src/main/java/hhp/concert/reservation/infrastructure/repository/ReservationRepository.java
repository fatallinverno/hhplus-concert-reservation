package hhp.concert.reservation.infrastructure.repository;

import hhp.concert.reservation.domain.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByUserUserSeq(Long userSeq);
}
