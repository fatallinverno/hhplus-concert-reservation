package hhp.concert.reservation.infrastructure.repository;

import hhp.concert.reservation.domain.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    @Query("SELECT DISTINCT r.reservationDate FROM ReservationEntity r WHERE r.isTemporary = false")
    List<String> findAvailableDates();

    @Query("SELECT r.seatEntity.seatNumber FROM ReservationEntity r WHERE r.reservationDate = :date")
    List<Integer> findReservedSeatNumbersByDate(@Param("date") String date);
}
