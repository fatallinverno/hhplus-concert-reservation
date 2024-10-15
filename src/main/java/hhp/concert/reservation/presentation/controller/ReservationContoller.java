package hhp.concert.reservation.presentation.controller;

import hhp.concert.reservation.application.service.ReservationService;
import hhp.concert.reservation.domain.entity.ReservationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservation")
public class ReservationContoller {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ReservationEntity reserveSeat(@RequestHeader("Authorization") String token, @RequestParam Long seatId, @RequestParam String date) {
        token = token.replace("Bearer ", "");
        return reservationService.reserveSeat(token, seatId, date);
    }

}
