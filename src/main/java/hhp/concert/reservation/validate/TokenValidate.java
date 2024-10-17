package hhp.concert.reservation.validate;

import hhp.concert.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenValidate {

    private JwtUtil jwtUtil;

    public boolean validateToken(String token, Long userId) {
        if(!jwtUtil.validateToken(token, userId)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        return false;
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = jwtUtil.extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

}
