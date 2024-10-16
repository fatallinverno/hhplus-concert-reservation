package hhp.concert.reservation.presentation.controller;

import hhp.concert.reservation.application.service.ReservationService;
import hhp.concert.reservation.domain.entity.ReservationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
public class ReservationContoller {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/availableDates")
    public List<String> getAvailableDates(Long concertId) {
        return reservationService.findAvailableDatesByConcert(concertId);
    }

    @GetMapping("/availableSeats")
    public List<Integer> getAvailableSeats(@RequestParam String date) {
        return reservationService.getAvailableSeats(date);
    }

    @PostMapping("/reserve")
    public ReservationEntity reserveSeat(@RequestParam Long userId, @RequestParam Long seatId) {
        return reservationService.reserveSeat(userId, seatId);
    }

}
