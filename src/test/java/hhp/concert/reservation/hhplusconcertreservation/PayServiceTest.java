package hhp.concert.reservation.hhplusconcertreservation;

import hhp.concert.reservation.application.service.PayService;
import hhp.concert.reservation.domain.entity.UserEntity;
import hhp.concert.reservation.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
    void testGetPay() {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setUserSeq(userId);
        user.setUserId("test");
        user.setPay(5000);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        int pay = payService.getPay(userId);
        assertEquals(5000, pay);

        verify(userRepository).findById(userId);
    }

    @Test
    void testChargePay() {
        Long userId = 1L;
        int amount = 3000;
        UserEntity mockUser = new UserEntity();
        mockUser.setUserSeq(userId);
        mockUser.setUserId("test");
        mockUser.setPay(5000);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        UserEntity updatedUser = payService.chargePay(userId, amount);
        assertEquals(8000, updatedUser.getPay());

        verify(userRepository).findById(userId);
        verify(userRepository).save(mockUser);
    }

}
