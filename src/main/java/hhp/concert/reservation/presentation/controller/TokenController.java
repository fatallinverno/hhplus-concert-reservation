package hhp.concert.reservation.presentation.controller;

import hhp.concert.reservation.application.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    // 대기열 토큰 발급
    @PostMapping
    public String issueToken(@RequestParam Long userSeq) {
        return tokenService.issueToken(userSeq);
    }

    // 대기 순번 조회
    @GetMapping("/queuePosition")
    public int getQueuePosition(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        if (tokenService.validateToken(token)) {
            return tokenService.getQueuePosition(token);
        } else {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }

}
