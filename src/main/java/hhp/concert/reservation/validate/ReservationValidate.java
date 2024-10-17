package hhp.concert.reservation.validate;

import hhp.concert.reservation.domain.entity.SeatEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservationValidate {

    public void validateSeat(SeatEntity seat) {
        if (!seat.isAvailable()) {
            throw new IllegalArgumentException("좌석이 예약 불가 상태입니다.");
        }
    }

}
