package hhp.concert.reservation.validate;

import hhp.concert.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenValidate {

    private JwtUtil jwtUtil;

    public boolean validateToken(String token) {
        if(token != null) {
            return isTokenValid(token);
        } else {
            throw new IllegalArgumentException("token값이 없습니다.");
        }
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
