package hhp.concert.reservation.application.service;


import hhp.concert.reservation.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TokenService {

    @Autowired
    private JwtUtil jwtUtil;

    public String issueToken(Long userSeq) {
        int queuePosition = new Random().nextInt(100);
        return jwtUtil.generateToken(userSeq, queuePosition);
    }

    public int getQueuePosition(String token) {
        Claims claims = jwtUtil.extractClaims(token);
        return (int) claims.get("queuePosition");
    }

    public boolean validateToken(String token) {
        return jwtUtil.isTokenValid(token);
    }

}
