package hhp.concert.reservation.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(Long userSeq, int queuePosition) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userSeq", userSeq);
        claims.put("queuePosition", queuePosition);
        claims.put("status", "pending");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))  // 토큰 생성 시간
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24시간 후 만료
                .signWith(secretKey, SignatureAlgorithm.HS256) // 생성된 비밀 키와 HS256 알고리즘으로 서명
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, Long userSeq) {
        Claims claims = extractClaims(token);
        Long tokenUserSeq = claims.get("userSeq", Long.class);
        return (userSeq.equals(tokenUserSeq)) && !isTokenExpired(token);
    }

}
