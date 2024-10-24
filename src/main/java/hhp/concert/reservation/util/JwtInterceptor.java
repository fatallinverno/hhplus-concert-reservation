package hhp.concert.reservation.util;

import hhp.concert.reservation.domain.entity.TokenEntity;
import hhp.concert.reservation.infrastructure.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    public JwtInterceptor(JwtUtil jwtUtil, TokenRepository tokenRepository) {
        this.jwtUtil = jwtUtil;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 없습니다.");
            return false;
        }

        String token = authorizationHeader.replace("Bearer ", "");

        try {
            Claims claims = jwtUtil.extractClaims(token);
            Long userId = claims.get("userId", Long.class);

            // 토큰 만료 체크
            if (jwtUtil.isTokenExpired(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다.");
                return false;
            }

            // 토큰 상태 체크 (COMPLETE 인지 확인)
            TokenEntity tokenEntity = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

            if ("COMPLETE".equals(tokenEntity.getStatus())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 이미 사용되었습니다.");
                return false;
            }

            // 시간 체크 로직: 토큰 발급 후 5분이 지났는지 확인
            long currentTime = System.currentTimeMillis();
            long issuedAtTime = claims.getIssuedAt().getTime();
            long timeDifference = currentTime - issuedAtTime;
            long maxTimeLimit = 5 * 60 * 1000;  // 5분 (밀리초)

            if (timeDifference > maxTimeLimit) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰 사용 시간이 초과되었습니다.");
                return false;
            }

            return true; // 요청을 계속 처리

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return false;
        }
    }

}
