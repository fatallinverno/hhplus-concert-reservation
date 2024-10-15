package hhp.concert.reservation.hhplusconcertreservation;

import hhp.concert.reservation.application.service.TokenService;
import hhp.concert.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TokenServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIssueToken() {
        Long userSeq = 1L;
        String fakeToken = "fake.jwt.token";

        // queuePosition에 어떤 값이 오더라도 fakeToken 반환
        when(jwtUtil.generateToken(eq(userSeq), anyInt())).thenReturn(fakeToken);

        String token = tokenService.issueToken(userSeq);
        assertEquals(fakeToken, token);

        // jwtUtil.generateToken 메서드가 호출되었는지 검증
        verify(jwtUtil, times(1)).generateToken(eq(userSeq), anyInt());
    }

    @Test
    void testGetQueuePosition() {
        String token = "fake.jwt.token";
        int expectedQueuePosition = 42;

        Claims claims = mock(Claims.class);
        when(claims.get("queuePosition")).thenReturn(expectedQueuePosition);
        when(jwtUtil.extractClaims(token)).thenReturn(claims);

        int queuePosition = tokenService.getQueuePosition(token);
        assertEquals(expectedQueuePosition, queuePosition);

        verify(jwtUtil, times(1)).extractClaims(token);
    }

}
