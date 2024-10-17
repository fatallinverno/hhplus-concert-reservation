package hhp.concert.reservation.hhplusconcertreservation;

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

import java.time.LocalDateTime;
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
        Long userSeq = 1L;
        int queuePosition = 1;

        when(jwtUtil.generateToken(userSeq, queuePosition)).thenReturn("jwtToken");

        String token = jwtUtil.generateToken(userSeq, queuePosition);
        assertNotNull(token);

        when(jwtUtil.extractClaims("jwtToken")).thenReturn(createMockClaims(userSeq, queuePosition));

        Claims claims = jwtUtil.extractClaims(token);
        assertEquals(userSeq, claims.get("userSeq", Long.class));
    }

    @Test
    @DisplayName("토큰 만료 검증")
    void testIsTokenExpired() {
        Long userSeq = 1L;
        int queuePosition = 1;

        when(jwtUtil.generateToken(userSeq, queuePosition)).thenReturn("mockToken");

        String token = jwtUtil.generateToken(userSeq, queuePosition);
        when(jwtUtil.isTokenExpired("mockToken")).thenReturn(false);

        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("토큰 유효성 검증")
    void testValidateToken() {
        Long userSeq = 1L;
        int queuePosition = 1;

        when(jwtUtil.generateToken(userSeq, queuePosition)).thenReturn("mockToken");
        when(jwtUtil.validateToken("mockToken", userSeq)).thenReturn(true);

        String token = jwtUtil.generateToken(userSeq, queuePosition);

        assertTrue(jwtUtil.validateToken(token, userSeq));
    }

    @Test
    @DisplayName("대기열 순번 조회")
    void testGetQueuePosition() {
        for (int i = 1; i <= 60; i++) {
            Long userSeq = (long) i;
            UserEntity user = new UserEntity();
            user.setUserSeq(userSeq);

            when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
            when(jwtUtil.generateToken(anyLong(), anyInt())).thenReturn("mockToken" + i);
            when(tokenRepository.save(any(TokenEntity.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

            tokenService.generateToken(userSeq);
        }

        // waitingQueue에 있는 사용자(51~60)의 대기열 순번 확인
        for (int i = 51; i <= 60; i++) {
            Long userSeq = (long) i;
            assertEquals(i - 50, tokenService.getQueuePosition(userSeq));
        }
    }

    @Test
    @DisplayName("대기열에서 입장 큐로 이동")
    void testMoveToReadyQueue() {
        for (int i = 1; i <= 60; i++) {
            Long userSeq = (long) i;
            UserEntity user = new UserEntity();
            user.setUserSeq(userSeq);

            when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
            when(jwtUtil.generateToken(anyLong(), anyInt())).thenReturn("jwtToken" + i);
            when(tokenRepository.save(any(TokenEntity.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

            tokenService.generateToken(userSeq);
        }

        assertEquals(50, tokenService.getReadyQueueSize());
        assertEquals(10, tokenService.getWaitingQueueSize());
    }

    private Claims createMockClaims(Long userSeq, int queuePosition) {
        Claims claims = Jwts.claims();
        claims.put("userSeq", userSeq);
        claims.put("queuePosition", queuePosition);
        claims.put("status", "pending");
        return claims;
    }

}
