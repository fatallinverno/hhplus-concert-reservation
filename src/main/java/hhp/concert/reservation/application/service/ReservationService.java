package hhp.concert.reservation.application.service;

import hhp.concert.reservation.domain.entity.ReservationEntity;
import hhp.concert.reservation.domain.entity.SeatEntity;
import hhp.concert.reservation.domain.entity.TokenEntity;
import hhp.concert.reservation.domain.entity.UserEntity;
import hhp.concert.reservation.infrastructure.repository.ConcertRepository;
import hhp.concert.reservation.infrastructure.repository.ReservationRepository;
import hhp.concert.reservation.infrastructure.repository.SeatRepository;
import hhp.concert.reservation.infrastructure.repository.UserRepository;
import hhp.concert.reservation.util.JwtUtil;
import hhp.concert.reservation.validate.ConcertValidate;
import hhp.concert.reservation.validate.ReservationValidate;
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
    private ConcertRepository concertRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TokenService tokenService;

    private ReservationValidate reservationValidate;

    private ConcertValidate concertValidate;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public List<String> findAvailableDatesByConcert(Long concertId) {
        boolean exists = concertRepository.existsById(concertId);
        concertValidate.validateConcertId(exists);

        List<String> dateCheck = reservationRepository.findAvailableDatesByConcert(concertId);

        return concertValidate.filterPastDates(dateCheck);
    }

    public List<Integer> getAvailableSeats(String date) {
        List<Integer> reservedSeats = reservationRepository.findReservedSeatNumbersByDate(date);

        return seatRepository.findAll()
                .stream()
                .map(seat -> seat.getSeatNumber())
                .filter(seatNumber -> !reservedSeats.contains(seatNumber))
                .collect(Collectors.toList());
    }

    public ReservationEntity reserveSeat(Long userId, Long seatId) {
        TokenEntity nextUserToken = tokenService.getNextInQueue();
        if (nextUserToken == null || !nextUserToken.getUserEntity().getUserSeq().equals(userId)) {
            throw new RuntimeException("현재 예약을 진행할 차례가 아닙니다.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        SeatEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("좌석을 찾을 수 없습니다."));

        reservationValidate.validateSeat(seat);

        seat.setAvailable(false);
        seatRepository.save(seat);

        tokenService.processNextInQueue();

        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserEntity(user);
        reservation.setSeatEntity(seat);
        reservation.setReservationDate(LocalDate.now());
        reservation.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        reservation.setTemporary(false);

        return reservationRepository.save(reservation);
    }

//    public ReservationEntity reserveSeat(String token, Long seatId, String date) {
//        Claims claims = jwtUtil.extractClaims(token);
//        Long userSeq = claims.get("userSeq", Long.class);
//
//        UserEntity user = userRepository.findById(userSeq)
//                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));
//        SeatEntity seat = seatRepository.findById(seatId)
//                .orElseThrow(() -> new RuntimeException("좌석이 없습니다."));
//
//        reservationValidate.validateSeat(seat);
//
//        ReservationEntity reservation = new ReservationEntity();
//        reservation.setUserEntity(user);
//        reservation.setSeatEntity(seat);
//        reservation.setReservationDate(LocalDate.parse(date));
//        reservation.setExpirationTime(LocalDateTime.now().plusMinutes(5)); // 5분 임시 배정
//        reservation.setTemporary(true);
//
//        seat.setAvailable(false);
//        seatRepository.save(seat);
//        ReservationEntity savedReservation = reservationRepository.save(reservation);
//
//        scheduler.schedule(() -> releaseTemporaryReservation(savedReservation.getReservationId()), 5, TimeUnit.MINUTES);
//
//        return savedReservation;
//    }

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
