package hhp.concert.reservation.hhplusconcertreservation;

import hhp.concert.reservation.application.service.ReservationService;
import hhp.concert.reservation.domain.entity.ReservationEntity;
import hhp.concert.reservation.domain.entity.SeatEntity;
import hhp.concert.reservation.domain.entity.UserEntity;
import hhp.concert.reservation.infrastructure.repository.ReservationRepository;
import hhp.concert.reservation.infrastructure.repository.SeatRepository;
import hhp.concert.reservation.infrastructure.repository.UserRepository;
import hhp.concert.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReserveSeatSuccess() {
        String token = "fake.jwt.token";
        Long userSeq = 1L;
        Long seatId = 1L;
        String date = "2024-10-15";

        UserEntity user = new UserEntity();
        user.setUserSeq(userSeq);

        SeatEntity seat = new SeatEntity();
        seat.setSeatId(seatId);
        seat.setAvailable(true);

        Claims claims = mock(Claims.class);
        when(claims.get("userSeq", Long.class)).thenReturn(userSeq);
        when(jwtUtil.extractClaims(token)).thenReturn(claims);
        when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        ReservationEntity reservation = new ReservationEntity();
        reservation.setReservationId(1L);
        reservation.setUserEntity(user);
        reservation.setSeatEntity(seat);
        reservation.setReservationDate(LocalDate.parse(date));
        reservation.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        reservation.setTemporary(true);

        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(reservation);

        ReservationEntity savedReservation = reservationService.reserveSeat(token, seatId, date);
        assertEquals(reservation, savedReservation);

        verify(userRepository).findById(userSeq);
        verify(seatRepository).findById(seatId);
        verify(seatRepository).save(seat);
        verify(reservationRepository).save(any(ReservationEntity.class));
    }

    @Test
    void testReserveSeatSeatNotAvailable() {
        String jwtToken = "jwt.token";
        Long userSeq = 1L;
        Long seatId = 1L;
        String date = "2024-10-15";

        UserEntity user = new UserEntity();
        user.setUserSeq(userSeq);

        SeatEntity seat = new SeatEntity();
        seat.setSeatId(seatId);
        seat.setAvailable(false);

        Claims claims = mock(Claims.class);
        when(claims.get("userSeq", Long.class)).thenReturn(userSeq);
        when(jwtUtil.extractClaims(jwtToken)).thenReturn(claims);
        when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        assertThrows(RuntimeException.class, () -> reservationService.reserveSeat(jwtToken, seatId, date), "좌석이 예약 불가 상태입니다.");

        verify(userRepository).findById(userSeq);
        verify(seatRepository).findById(seatId);
        verify(seatRepository, never()).save(any(SeatEntity.class));
        verify(reservationRepository, never()).save(any(ReservationEntity.class));
    }

}
