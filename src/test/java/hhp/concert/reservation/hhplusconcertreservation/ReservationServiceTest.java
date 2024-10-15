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
        Long concertId = 1L;
        List<String> mockDates = Arrays.asList("2023-10-15", "2023-10-16", "2023-10-17");
        when(reservationRepository.findAvailableDatesByConcert(concertId)).thenReturn(mockDates);

        List<String> availableDates = reservationService.findAvailableDatesByConcert(concertId);
        assertEquals(mockDates, availableDates);

        verify(reservationRepository).findAvailableDatesByConcert(concertId);
    }

    @Test
    @DisplayName("예약 가능 좌석 조회")
    void testGetAvailableSeats() {
        Long seatId = 1L;
        String date = "2023-10-15";
        List<Integer> reservedSeats = Arrays.asList(1, 2, 3);

        // 예약된 좌석 번호 Mock 설정
        when(reservationRepository.findReservedSeatNumbersByDate(date)).thenReturn(reservedSeats);

        // 전체 좌석을 SeatEntity 객체로 생성하고, seatNumber 필드를 명시적으로 설정
        List<SeatEntity> allSeats = Arrays.asList(
                createSeatEntity(seatId, 1),
                createSeatEntity(seatId, 2),
                createSeatEntity(seatId, 3),
                createSeatEntity(seatId, 4),
                createSeatEntity(seatId, 5)
        );

        // seatRepository.findAll()이 올바른 SeatEntity 객체 리스트를 반환하도록 Mock 설정
        when(seatRepository.findAll()).thenReturn(allSeats);

        // 예약 가능한 좌석 조회
        List<Integer> availableSeats = reservationService.getAvailableSeats(date);
        List<Integer> expectedSeats = Arrays.asList(4, 5);

        // 예상 값과 실제 값 비교
        assertEquals(expectedSeats, availableSeats);

        verify(reservationRepository, times(1)).findReservedSeatNumbersByDate(date);
        verify(seatRepository, times(1)).findAll();

    }

    // SeatEntity 객체를 생성하고 seatNumber 필드를 설정하는 헬퍼 메서드
    private SeatEntity createSeatEntity(Long seatId, int seatNumber) {
        SeatEntity seat = new SeatEntity();
        seat.setSeatId(seatId);
        seat.setSeatNumber(seatNumber);
        return seat;
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
