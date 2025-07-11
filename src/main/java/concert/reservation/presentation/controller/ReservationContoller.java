package concert.reservation.presentation.controller;

import concert.reservation.domain.entity.ReservationEntity;
import concert.reservation.validate.TokenValidate;
import concert.reservation.application.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservation")
@Tag(name = "reservation Controller", description = "예약 API")
public class ReservationContoller {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TokenValidate tokenValidate;

    @PostMapping("/reserve")
    @Operation(summary = "좌석 예약", description = "콘서트 좌석을 예약합니다.")
    public ReservationEntity reserveSeat(@RequestParam Long userId, @RequestParam Long seatId, @RequestParam String token) {
        tokenValidate.validateToken(token, userId);

        return reservationService.redisReserveSeat(userId, seatId);
    }

}
