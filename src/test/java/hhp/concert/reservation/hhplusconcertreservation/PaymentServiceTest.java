package hhp.concert.reservation.hhplusconcertreservation;

import hhp.concert.reservation.application.service.PaymentService;
import hhp.concert.reservation.domain.entity.PaymentEntity;
import hhp.concert.reservation.domain.entity.SeatEntity;
import hhp.concert.reservation.domain.entity.UserEntity;
import hhp.concert.reservation.infrastructure.repository.PaymentRepository;
import hhp.concert.reservation.infrastructure.repository.SeatRepository;
import hhp.concert.reservation.infrastructure.repository.UserRepository;
import hhp.concert.reservation.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("결재")
    void testProcessPayment() {
        Long userId = 1L;
        Long seatId = 1L;
        int amount = 1000;
        String token = "TokenTest";

        UserEntity user = new UserEntity();
        user.setUserSeq(userId);
        user.setUserId("test");

        SeatEntity seat = new SeatEntity();
        seat.setSeatId(seatId);
        seat.setSeatNumber(10);
        seat.setAvailable(true);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(seatRepository.findById(seatId)).thenReturn(java.util.Optional.of(seat));
        doNothing().when(jwtUtil).expirationToken(token);

        paymentService.processPayment(userId, seatId, amount, token);

        ArgumentCaptor<PaymentEntity> captor = ArgumentCaptor.forClass(PaymentEntity.class);
        verify(paymentRepository).save(captor.capture());

        PaymentEntity savedPayment = captor.getValue();
        assertEquals(amount, savedPayment.getAmount());
        assertEquals(user, savedPayment.getUser());
        assertEquals(seat, savedPayment.getSeat());
        assertEquals(PaymentEntity.PaymentStatus.COMPLETED, savedPayment.getPaymentStatus());

        verify(userRepository).findById(userId);
        verify(seatRepository).findById(seatId);
        verify(seatRepository).save(seat);
        verify(jwtUtil).expirationToken(token);
    }

}
