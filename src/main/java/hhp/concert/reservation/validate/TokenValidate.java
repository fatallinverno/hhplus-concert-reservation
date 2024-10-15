package hhp.concert.reservation.validate;

import hhp.concert.reservation.util.JwtUtil;

public class TokenValidate {

    private JwtUtil jwtUtil;

    public boolean validateToken(String token) {

        if(token != null) {
            return jwtUtil.isTokenValid(token);
        } else {
            throw new IllegalArgumentException("token값이 없습니다.");
        }

    }

}
