package hhp.concert.reservation.hhplusconcertreservation.unit;

import hhp.concert.reservation.application.service.PayService;
import hhp.concert.reservation.domain.entity.UserEntity;
import hhp.concert.reservation.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PayServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PayService payService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("잔액 조회")
    void testGetPay() {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setUserName("test");
        user.setPay(5000);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        int pay = payService.getPay(userId);
        assertEquals(5000, pay);

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("잔액 충전")
    void testChargePay() {
        Long userId = 1L;
        int amount = 3000;
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setUserName("test");
        user.setPay(5000);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserEntity updatedUser = payService.chargePay(userId, amount);
        assertEquals(8000, updatedUser.getPay());

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

}
