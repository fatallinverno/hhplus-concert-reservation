package hhp.concert.reservation.presentation.controller;

import hhp.concert.reservation.application.service.ConcertService;
import hhp.concert.reservation.application.service.ReservationService;
import hhp.concert.reservation.domain.entity.ReservationEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@Tag(name = "reservation Controller", description = "예약 API")
public class ReservationContoller {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/reserve")
    @Operation(summary = "좌석 예약", description = "콘서트 좌석을 예약합니다.")
    public ReservationEntity reserveSeat(@RequestParam Long userId, @RequestParam Long seatId) {
        return reservationService.reserveSeat(userId, seatId);
    }

}
