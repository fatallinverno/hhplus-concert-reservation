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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("예약 가능 날짜 조회")
    void testGetAvailableDates() {
        List<String> mockDates = Arrays.asList("2023-10-15", "2023-10-16", "2023-10-17");
        when(reservationRepository.findAvailableDates()).thenReturn(mockDates);

        List<String> availableDates = reservationService.getAvailableDates();
        assertEquals(mockDates, availableDates);

        verify(reservationRepository, times(1)).findAvailableDates();
    }

    @Test
    @DisplayName("예약 가능 좌석 조회")
    void testGetAvailableSeats() {
        String date = "2023-10-15";
        List<Integer> reservedSeats = Arrays.asList(1, 2, 3);
        when(reservationRepository.findReservedSeatNumbersByDate(date)).thenReturn(reservedSeats);

        List<SeatEntity> allSeats = Arrays.asList(
                new SeatEntity(1L, 1, true),
                new SeatEntity(2L, 2, true),
                new SeatEntity(3L, 3, true),
                new SeatEntity(4L, 4, true),
                new SeatEntity(5L, 5, true)
        );
        when(seatRepository.findAll()).thenReturn(allSeats);

        List<Integer> availableSeats = reservationService.getAvailableSeats(date);
        List<Integer> expectedSeats = Arrays.asList(4, 5);

        assertEquals(expectedSeats, availableSeats);

        verify(reservationRepository, times(1)).findReservedSeatNumbersByDate(date);
        verify(seatRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("좌석 예약")
    void testReserveSeat() {
        String jwtToken = "jwt.token";
        Long userSeq = 1L;
        Long seatId = 1L;
        int seatNumber = 1;
        String date = "2023-10-15";

        UserEntity user = new UserEntity();
        user.setUserSeq(userSeq);

        SeatEntity seat = new SeatEntity();
        seat.setSeatId(seatId);
        seat.setSeatNumber(seatNumber);
        seat.setAvailable(true);

        Claims claims = mock(Claims.class);
        when(claims.get("userSeq", Long.class)).thenReturn(userSeq);
        when(jwtUtil.extractClaims(jwtToken)).thenReturn(claims);
        when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        ReservationEntity reservation = new ReservationEntity();
        reservation.setReservationDate(LocalDate.parse(date));
        reservation.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        reservation.setTemporary(true);

        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(reservation);

        ReservationEntity savedReservation = reservationService.reserveSeat(jwtToken, seatId, date);
        assertNotNull(savedReservation);

        verify(userRepository).findById(userSeq);
        verify(seatRepository).findById(seatId);
        verify(seatRepository).findById((long) seatNumber);
        verify(seatRepository).save(seat);
        verify(reservationRepository).save(any(ReservationEntity.class));
    }

}
