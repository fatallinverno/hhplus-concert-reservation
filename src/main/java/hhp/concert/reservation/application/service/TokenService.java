package hhp.concert.reservation.application.service;


import hhp.concert.reservation.util.JwtUtil;
import hhp.concert.reservation.validate.TokenValidate;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TokenService {

    @Autowired
    private JwtUtil jwtUtil;

    private TokenValidate tokenValidate;

    public String issueToken(Long userSeq) {
        int queuePosition = new Random().nextInt(100);
        return jwtUtil.generateToken(userSeq, queuePosition);
    }

    public int getQueuePosition(String token) {
        tokenValidate.validateToken(token);

        Claims claims = jwtUtil.extractClaims(token);
        return (int) claims.get("queuePosition");
    }

}