package hhp.concert.reservation.application.service;

import hhp.concert.reservation.domain.entity.SeatEntity;
import hhp.concert.reservation.infrastructure.repository.ConcertRepository;
import hhp.concert.reservation.infrastructure.repository.SeatRepository;
import hhp.concert.reservation.validate.ConcertValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConcertService {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private SeatRepository seatRepository;

    private ConcertValidate concertValidate;

    public List<LocalDate> findAvailableDatesByConcert(Long concertId) {
        boolean exists = concertRepository.existsById(concertId);
        concertValidate.validateConcertId(exists);

        List<LocalDate> availableDates = concertRepository.findAvailableConcertDates(concertId);

        return availableDates;
    }

    public List<Integer> getAvailableSeats(Long concertId, String date) {
        List<Integer> reservedSeats = concertRepository.findReservedSeatNumbersByDateAndConcertId(date, concertId);

        return seatRepository.findAll()
                .stream()
                .filter(SeatEntity::isAvailable)
                .filter(seat -> !reservedSeats.contains(seat.getSeatNumber()))
                .map(SeatEntity::getSeatNumber)
                .collect(Collectors.toList());
    }

}
