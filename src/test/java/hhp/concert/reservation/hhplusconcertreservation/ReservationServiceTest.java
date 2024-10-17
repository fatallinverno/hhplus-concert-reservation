package hhp.concert.reservation.hhplusconcertreservation;

import hhp.concert.reservation.application.service.ConcertService;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Mock
    private ReservationValidate reservationValidate;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ConcertValidate concertValidate;

    @InjectMocks
    private ConcertService concertService;

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
        LocalDate fixedToday = LocalDate.of(2024, 10, 18); // 고정된 날짜 설정

        List<LocalDate> allDates = Arrays.asList(
                LocalDate.of(2024, 10, 10),
                LocalDate.of(2024, 10, 20),
                LocalDate.of(2024, 10, 21)
        );
        List<LocalDate> expectedDates = Arrays.asList(
                LocalDate.of(2024, 10, 20),
                LocalDate.of(2024, 10, 21)
        );

        when(concertRepository.findAvailableConcertDates(concertId)).thenReturn(allDates);

        // 실제로 필터링을 수행하여 사용
        List<LocalDate> filteredDates = allDates.stream()
                .filter(date -> !date.isBefore(fixedToday))
                .collect(Collectors.toList());

        List<LocalDate> availableDates = concertService.findAvailableDatesByConcert(concertId);
        assertEquals(expectedDates, filteredDates);  // 기대되는 필터링 결과와 비교
    }

    @Test
    @DisplayName("예약 가능 좌석 조회")
    void testGetAvailableSeats() {
        Long concertId = 1L;
        Long seatId = 1L;
        String date = "2023-10-20";
        List<Integer> reservedSeats = Arrays.asList(1, 2, 3);

        // 날짜와 콘서트 ID를 기반으로 예약된 좌석을 반환하도록 Mock 설정
        when(concertRepository.findReservedSeatNumbersByDateAndConcertId(date, concertId)).thenReturn(reservedSeats);

        // 전체 좌석 목록 생성 (4번과 5번 좌석은 활성화된 상태로 가정)
        List<SeatEntity> allSeats = Arrays.asList(
                createSeatEntity(seatId, 1, false),  // 예약된 좌석
                createSeatEntity(seatId + 1, 2, false),  // 예약된 좌석
                createSeatEntity(seatId + 2, 3, false),  // 예약된 좌석
                createSeatEntity(seatId + 3, 4, true),  // 활성화된 좌석
                createSeatEntity(seatId + 4, 5, true)  // 활성화된 좌석
        );

        when(seatRepository.findAll()).thenReturn(allSeats);

        // 예약 가능한 좌석 조회
        List<Integer> availableSeats = concertService.getAvailableSeats(concertId, date);
        List<Integer> expectedSeats = Arrays.asList(4, 5);

        // 예상 결과와 일치하는지 확인
        assertEquals(expectedSeats, availableSeats);

        // 각 메서드 호출 횟수 검증
        verify(concertRepository, times(1)).findReservedSeatNumbersByDateAndConcertId(date, concertId);
        verify(seatRepository, times(1)).findAll();
    }

    private SeatEntity createSeatEntity(Long seatId, int seatNumber, boolean isAvailable) {
        SeatEntity seat = new SeatEntity();
        seat.setSeatId(seatId);
        seat.setSeatNumber(seatNumber);
        seat.setAvailable(isAvailable);
        return seat;
    }

    @Test
    @DisplayName("좌석 예약")
    void testReserveSeat() {
        Long userId = 1L;
        Long seatId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        SeatEntity seat = new SeatEntity();
        seat.setSeatId(seatId);
        seat.setAvailable(true);

        TokenEntity token = new TokenEntity();
        token.setUserEntity(user);

        when(tokenService.getNextInQueue()).thenReturn(token);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(seatRepository.findByIdForReservation(seatId)).thenReturn(Optional.of(seat));
        when(reservationRepository.save(any(ReservationEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        doNothing().when(reservationValidate).validateSeat(seat);

        ReservationEntity reservation = reservationService.reserveSeat(userId, seatId);

        assertEquals(user, reservation.getUserEntity(), "예약된 사용자 정보가 일치하지 않습니다.");
        assertEquals(seat, reservation.getSeatEntity(), "예약된 좌석 정보가 일치하지 않습니다.");
        assertFalse(reservation.isTemporary(), "좌석이 임시 예약 상태로 설정되었습니다.");

        verify(seatRepository, times(1)).save(seat);
        verify(tokenService, times(1)).processNextInQueue();
        verify(reservationValidate, times(1)).validateSeat(seat);

        assertFalse(seat.isAvailable(), "좌석이 예약 상태로 변경되지 않았습니다.");
    }

}
