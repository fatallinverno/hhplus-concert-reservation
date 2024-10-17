package hhp.concert.reservation.hhplusconcertreservation;

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
        Long seatId = 1L;
        int amount = 1000;
        String token = "TokenTest";

        UserEntity user = new UserEntity();
        user.setUserSeq(userId);
        user.setUserId("testUser");

        SeatEntity seat = new SeatEntity();
        seat.setSeatId(seatId);
        seat.setSeatNumber(10);
        seat.setAvailable(true);

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUserEntity(user);
        tokenEntity.setStatus("pending");

        ReservationEntity reservation = new ReservationEntity();
        reservation.setReservationId(1L);
        reservation.setUserEntity(user);
        reservation.setSeatEntity(seat);
        reservation.setTemporary(true);
        reservation.setExpirationTime(LocalDateTime.now().minusMinutes(1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));
        when(reservationRepository.findByUserEntityAndSeatEntityAndTemporary(user, seat, true))
                .thenReturn(Optional.of(reservation));

        paymentService.processPayment(userId, seatId, amount, token);

        ArgumentCaptor<PaymentEntity> paymentCaptor = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository).save(paymentCaptor.capture());

        PaymentEntity savedPayment = paymentCaptor.getValue();
        assertEquals(amount, savedPayment.getAmount());
        assertEquals(user, savedPayment.getUser());
        assertEquals(seat, savedPayment.getSeat());
        assertEquals(PaymentEntity.PaymentStatus.COMPLETED, savedPayment.getPaymentStatus());

        assertFalse(seat.isAvailable());
        verify(seatRepository).save(seat);
        verify(tokenService).completeToken(tokenEntity.getTokenId());
    }

}
