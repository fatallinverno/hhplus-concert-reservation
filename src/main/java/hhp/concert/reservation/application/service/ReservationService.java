package hhp.concert.reservation.application.service;

import hhp.concert.reservation.domain.entity.ReservationEntity;
import hhp.concert.reservation.domain.entity.SeatEntity;
import hhp.concert.reservation.domain.entity.UserEntity;
import hhp.concert.reservation.infrastructure.repository.ReservationRepository;
import hhp.concert.reservation.infrastructure.repository.SeatRepository;
import hhp.concert.reservation.infrastructure.repository.UserRepository;
import hhp.concert.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReservationService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    public ReservationEntity reserveSeat(String token, Long seatId, String date) {
        Claims claims = jwtUtil.extractClaims(token);
        Long userSeq = claims.get("userSeq", Long.class);

        UserEntity user = userRepository.findById(userSeq)
                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));
        SeatEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("좌석이 없습니다."));

        if (!seat.isAvailable()) {
            throw new RuntimeException("좌석이 예약 불가 상태입니다.");
        }

        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserEntity(user);
        reservation.setSeatEntity(seat);
        reservation.setReservationDate(LocalDate.parse(date));
        reservation.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        reservation.setTemporary(true);

        seat.setAvailable(false);
        seatRepository.save(seat);

        return reservationRepository.save(reservation);
    }

}
