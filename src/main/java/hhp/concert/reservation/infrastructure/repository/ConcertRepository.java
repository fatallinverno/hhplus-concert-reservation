package hhp.concert.reservation.infrastructure.repository;


import hhp.concert.reservation.domain.entity.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ConcertRepository extends JpaRepository<ConcertEntity, Long> {

    @Query("SELECT c.concertDate FROM ConcertEntity c WHERE c.concertId = :concertId AND c.concertDate >= CURRENT_DATE")
    List<LocalDate> findAvailableConcertDates(@Param("concertId") Long concertId);

    @Query("SELECT r.seatEntity.seatNumber FROM ReservationEntity r WHERE r.reservationDate = :date AND r.concertEntity.concertId = :concertId")
    List<Integer> findReservedSeatNumbersByDateAndConcertId(@Param("date") String date, @Param("concertId") Long concertId);
}
