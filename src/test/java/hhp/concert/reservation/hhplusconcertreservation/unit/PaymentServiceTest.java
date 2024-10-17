package hhp.concert.reservation.hhplusconcertreservation.unit;

import hhp.concert.reservation.application.service.PaymentService;
import hhp.concert.reservation.application.service.TokenService;
import hhp.concert.reservation.domain.entity.*;
import hhp.concert.reservation.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private ConcertRepository concertRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("결제 처리 후 결제 내역 생성 및 토큰 만료")
    void testProcessPayment() {
        Long userId = 1L;
        Long concertId = 1L;
        Long seatId = 1L;
        int amount = 1000;
        String token = "TokenTest";

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setUserName("testUser");

        ConcertEntity concert = new ConcertEntity();
        concert.setConcertId(concertId);
        concert.setConcertName("testConcert");

        SeatEntity seat = new SeatEntity();
        seat.setSeatId(seatId);
        seat.setSeatNumber(10);
        seat.setAvailable(true);

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUserEntity(user);
        tokenEntity.setStatus("pending");

        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserEntity(user);
        reservation.setSeatEntity(seat);
        reservation.setConcertEntity(concert);
        reservation.setTemporary(true);
        reservation.setExpirationTime(LocalDateTime.now().minusMinutes(1));

        // Mock 설정
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));
        when(reservationRepository.findByUserEntityAndConcertEntityAndSeatEntityAndIsTemporary(user, concert, seat, true))
                .thenReturn(Optional.of(reservation));

        paymentService.processPayment(userId, concertId, seatId, amount, token);

        // 결제 내역 확인
        ArgumentCaptor<PaymentEntity> paymentCaptor = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        PaymentEntity savedPayment = paymentCaptor.getValue();

        assertEquals(amount, savedPayment.getAmount(), "결제 금액이 일치하지 않습니다.");
        assertEquals(user, savedPayment.getUserEntity(), "사용자가 일치하지 않습니다.");
        assertEquals(seat, savedPayment.getSeat(), "좌석이 일치하지 않습니다.");
        assertEquals(PaymentEntity.PaymentStatus.COMPLETED, savedPayment.getPaymentStatus(), "결제 상태가 일치하지 않습니다.");

        // 좌석 예약 상태 확인
        assertFalse(seat.isAvailable(), "좌석이 예약 상태로 변경되지 않았습니다.");
        verify(seatRepository).save(seat);

        // 토큰 완료 상태 확인
        verify(tokenService).completeToken(tokenEntity.getTokenId());
    }

}
