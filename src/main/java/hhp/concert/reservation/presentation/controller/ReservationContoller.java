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
    public List<String> getAvailableDates() {
        return reservationService.getAvailableDates();
    }

    @GetMapping("/availableSeats")
    public List<Integer> getAvailableSeats(@RequestParam String date) {
        return reservationService.getAvailableSeats(date);
    }

    @PostMapping
    public ReservationEntity reserveSeat(@RequestHeader("Authorization") String token, @RequestParam Long seatId, @RequestParam String date) {
        token = token.replace("Bearer ", "");
        return reservationService.reserveSeat(token, seatId, date);
    }

}
