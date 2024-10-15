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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public List<String> getAvailableDates() {
        return reservationRepository.findAvailableDates();
    }

    public List<Integer> getAvailableSeats(String date) {
        List<Integer> reservedSeats = reservationRepository.findReservedSeatNumbersByDate(date);

        return seatRepository.findAll()
                .stream()
                .map(seat -> seat.getSeatNumber()) // 모든 좌석 번호를 가져와서
                .filter(seatNumber -> !reservedSeats.contains(seatNumber)) // 예약된 좌석을 제외
                .collect(Collectors.toList());
    }

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
        reservation.setExpirationTime(LocalDateTime.now().plusMinutes(5)); // 5분 임시 배정
        reservation.setTemporary(true);

        seat.setAvailable(false);
        seatRepository.save(seat);
        ReservationEntity savedReservation = reservationRepository.save(reservation);

        scheduler.schedule(() -> releaseTemporaryReservation(savedReservation.getReservationId()), 5, TimeUnit.MINUTES);

        return savedReservation;
    }

    private void releaseTemporaryReservation(Long reservationId) {
        Optional<ReservationEntity> reservationOpt = reservationRepository.findById(reservationId);
        reservationOpt.ifPresent(reservation -> {
            if (reservation.isTemporary() && reservation.getExpirationTime().isBefore(LocalDateTime.now())) {
                SeatEntity seat = reservation.getSeatEntity();
                seat.setAvailable(true); // 좌석을 예약 가능 상태로 변경
                seatRepository.save(seat);
                reservationRepository.delete(reservation); // 임시 예약 삭제
                System.out.println("임시 예약이 해제되었습니다. 좌석 ID: " + seat.getSeatId());
            }
        });
    }

}