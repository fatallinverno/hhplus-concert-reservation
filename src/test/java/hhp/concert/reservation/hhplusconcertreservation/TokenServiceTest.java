package hhp.concert.reservation.hhplusconcertreservation;

import hhp.concert.reservation.application.service.TokenService;
import hhp.concert.reservation.domain.entity.TokenEntity;
import hhp.concert.reservation.domain.entity.UserEntity;
import hhp.concert.reservation.infrastructure.repository.UserRepository;
import hhp.concert.reservation.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TokenServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateToken() {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setUserSeq(userId);
        user.setUserId("testUser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(userId, 1)).thenReturn("mockToken");

        TokenEntity token = tokenService.generateToken(userId);

        assertEquals(userId, token.getUserEntity().getUserSeq());
        assertEquals("mockToken", token.getToken());
        verify(userRepository, times(1)).findById(userId);
        verify(jwtUtil, times(1)).generateToken(userId, 1);
    }

    @Test
    @DisplayName("대기열 순번 조회")
    void testGetQueuePosition() {
        Long userSeq1 = 1L;
        Long userSeq2 = 2L;

        UserEntity user1 = new UserEntity();
        user1.setUserSeq(userSeq1);
        user1.setUserId("testUser1");

        UserEntity user2 = new UserEntity();
        user2.setUserSeq(userSeq2);
        user2.setUserId("testUser2");

        when(userRepository.findById(userSeq1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(userSeq2)).thenReturn(Optional.of(user2));
        when(jwtUtil.generateToken(anyLong(), anyInt())).thenReturn("mockToken");

        // 첫 번째 사용자 추가 및 대기열 확인
        tokenService.generateToken(userSeq1);
        assertEquals(1, tokenService.getWaitingQueueSize(), "첫 번째 사용자 추가 후 대기열 크기가 예상과 다릅니다.");

        // 두 번째 사용자 추가 및 대기열 확인
        tokenService.generateToken(userSeq2);
        assertEquals(2, tokenService.getWaitingQueueSize(), "두 번째 사용자 추가 후 대기열 크기가 예상과 다릅니다.");

        // 대기열 순번 확인
        int position1 = tokenService.getQueuePosition(userSeq1);
        int position2 = tokenService.getQueuePosition(userSeq2);
        assertEquals(1, position1, "첫 번째 사용자 대기열 순번이 예상과 다릅니다.");
        assertEquals(2, position2, "두 번째 사용자 대기열 순번이 예상과 다릅니다.");
    }

    @Test
    @DisplayName("대기열에서 입장 큐로 이동")
    void testMoveToReadyQueue() {
        for (int i = 1; i <= 55; i++) {
            Long userSeq = (long) i;
            UserEntity user = new UserEntity();
            user.setUserSeq(userSeq);
            user.setUserId("testUser" + i);

            when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
            when(jwtUtil.generateToken(anyLong(), anyInt())).thenReturn("mockToken" + i);

            tokenService.generateToken(userSeq);
        }

        // 대기열 및 입장 가능한 큐 크기 확인
        assertEquals(50, tokenService.getReadyQueueSize(), "입장 가능한 큐 크기가 예상과 다릅니다.");
        assertEquals(55, tokenService.getWaitingQueueSize() + tokenService.getReadyQueueSize(), "대기열 크기가 예상과 다릅니다.");
    }

}
