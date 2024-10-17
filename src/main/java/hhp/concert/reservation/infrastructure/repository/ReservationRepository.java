package hhp.concert.reservation.infrastructure.repository;

import hhp.concert.reservation.domain.entity.ConcertEntity;
import hhp.concert.reservation.domain.entity.ReservationEntity;
import hhp.concert.reservation.domain.entity.SeatEntity;
import hhp.concert.reservation.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    Optional<ReservationEntity> findByUserEntityAndConcertEntityAndSeatEntityAndIsTemporary(UserEntity userEntity, ConcertEntity concertEntity, SeatEntity seatEntity, boolean isTemporary);
}
