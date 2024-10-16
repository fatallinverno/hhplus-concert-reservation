package hhp.concert.reservation.hhplusconcertreservation;

import hhp.concert.reservation.application.service.ReservationService;
import hhp.concert.reservation.application.service.TokenService;
import hhp.concert.reservation.domain.entity.ReservationEntity;
import hhp.concert.reservation.domain.entity.SeatEntity;
import hhp.concert.reservation.domain.entity.TokenEntity;
import hhp.concert.reservation.domain.entity.UserEntity;
import hhp.concert.reservation.infrastructure.repository.ConcertRepository;
import hhp.concert.reservation.infrastructure.repository.ReservationRepository;
import hhp.concert.reservation.infrastructure.repository.SeatRepository;
import hhp.concert.reservation.infrastructure.repository.UserRepository;
import hhp.concert.reservation.validate.ConcertValidate;
import hhp.concert.reservation.validate.ReservationValidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationValidate reservationValidate;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ConcertValidate concertValidate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("예약 가능 날짜 조회")
    void testGetAvailableDates() {
        Long concertId = 1L;
        List<String> allDates = Arrays.asList("2024-10-10", "2024-10-16", "2024-10-20");
        List<String> filteredDates = Arrays.asList("2024-10-16", "2024-10-20");

        when(concertRepository.existsById(concertId)).thenReturn(true);
        doNothing().when(concertValidate).validateConcertId(true);
        when(reservationRepository.findAvailableDatesByConcert(concertId)).thenReturn(allDates);
        when(concertValidate.filterPastDates(allDates)).thenReturn(filteredDates);

        List<String> availableDates = reservationService.findAvailableDatesByConcert(concertId);
        assertEquals(filteredDates, availableDates);

        verify(concertRepository).existsById(concertId);
        verify(concertValidate).validateConcertId(true);
        verify(reservationRepository).findAvailableDatesByConcert(concertId);
        verify(concertValidate).filterPastDates(allDates);
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
        Long userId = 1L;
        Long seatId = 1L;
        UserEntity user = new UserEntity();
        user.setUserSeq(userId);
        SeatEntity seat = new SeatEntity();
        seat.setSeatId(seatId);
        seat.setAvailable(true);

        TokenEntity token = new TokenEntity();
        token.setUserEntity(user);

        when(tokenService.getNextInQueue()).thenReturn(token);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Mock ReservationValidate의 메서드 동작 정의
        doNothing().when(reservationValidate).validateSeat(seat);

        ReservationEntity reservation = reservationService.reserveSeat(userId, seatId);

        assertEquals(user, reservation.getUserEntity());
        assertEquals(seat, reservation.getSeatEntity());
        assertEquals(false, reservation.isTemporary());
        verify(seatRepository, times(1)).save(seat);
        verify(tokenService, times(1)).processNextInQueue();
        verify(reservationValidate, times(1)).validateSeat(seat); // ReservationValidate 호출 확인
    }

    @Test
    void testReserveSeatNotAvailable() {
        Long userId = 1L;
        Long seatId = 1L;
        UserEntity user = new UserEntity();
        user.setUserSeq(userId);
        SeatEntity seat = new SeatEntity();
        seat.setSeatId(seatId);
        seat.setAvailable(false); // 이미 예약된 좌석

        TokenEntity token = new TokenEntity();
        token.setUserEntity(user);

        when(tokenService.getNextInQueue()).thenReturn(token);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));

        // Mock ReservationValidate의 예외 동작 정의
        doThrow(new RuntimeException("좌석이 이미 예약되었습니다.")).when(reservationValidate).validateSeat(seat);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            reservationService.reserveSeat(userId, seatId);
        });

        assertEquals("좌석이 이미 예약되었습니다.", exception.getMessage());
    }

}
