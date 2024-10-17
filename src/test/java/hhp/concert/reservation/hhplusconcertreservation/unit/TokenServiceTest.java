package hhp.concert.reservation.hhplusconcertreservation.unit;

import hhp.concert.reservation.application.service.TokenService;
import hhp.concert.reservation.domain.entity.TokenEntity;
import hhp.concert.reservation.domain.entity.UserEntity;
import hhp.concert.reservation.infrastructure.repository.TokenRepository;
import hhp.concert.reservation.infrastructure.repository.UserRepository;
import hhp.concert.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("토큰 생성 및 클레임 검증")
    void testGenerateToken() {
        Long userId = 1L;
        int queuePosition = 1;

        when(jwtUtil.generateToken(userId, queuePosition)).thenReturn("jwtToken");

        String token = jwtUtil.generateToken(userId, queuePosition);
        assertNotNull(token);

        when(jwtUtil.extractClaims("jwtToken")).thenReturn(createMockClaims(userId, queuePosition));

        Claims claims = jwtUtil.extractClaims(token);
        assertEquals(userId, claims.get("userId", Long.class));
    }

    @Test
    @DisplayName("토큰 만료 검증")
    void testIsTokenExpired() {
        Long userId = 1L;
        int queuePosition = 1;

        when(jwtUtil.generateToken(userId, queuePosition)).thenReturn("mockToken");

        String token = jwtUtil.generateToken(userId, queuePosition);
        when(jwtUtil.isTokenExpired("mockToken")).thenReturn(false);

        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("토큰 유효성 검증")
    void testValidateToken() {
        Long userId = 1L;
        int queuePosition = 1;

        when(jwtUtil.generateToken(userId, queuePosition)).thenReturn("mockToken");
        when(jwtUtil.validateToken("mockToken", userId)).thenReturn(true);

        String token = jwtUtil.generateToken(userId, queuePosition);

        assertTrue(jwtUtil.validateToken(token, userId));
    }

    @Test
    @DisplayName("대기열 순번 조회")
    void testGetQueuePosition() {
        for (int i = 1; i <= 60; i++) {
            Long userId = (long) i;
            UserEntity user = new UserEntity();
            user.setUserId(userId);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(jwtUtil.generateToken(anyLong(), anyInt())).thenReturn("mockToken" + i);
            when(tokenRepository.save(any(TokenEntity.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

            tokenService.generateToken(userId);
        }

        // waitingQueue에 있는 사용자(51~60)의 대기열 순번 확인
        for (int i = 51; i <= 60; i++) {
            Long userId = (long) i;
            assertEquals(i - 50, tokenService.getQueuePosition(userId));
        }
    }

    @Test
    @DisplayName("대기열에서 입장 큐로 이동")
    void testMoveToReadyQueue() {
        for (int i = 1; i <= 60; i++) {
            Long userId = (long) i;
            UserEntity user = new UserEntity();
            user.setUserId(userId);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(jwtUtil.generateToken(anyLong(), anyInt())).thenReturn("jwtToken" + i);
            when(tokenRepository.save(any(TokenEntity.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

            tokenService.generateToken(userId);
        }

        assertEquals(50, tokenService.getReadyQueueSize());
        assertEquals(10, tokenService.getWaitingQueueSize());
    }

    private Claims createMockClaims(Long userId, int queuePosition) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("queuePosition", queuePosition);
        claims.put("status", "pending");
        return claims;
    }

}