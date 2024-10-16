package hhp.concert.reservation.presentation.controller;

import hhp.concert.reservation.application.service.TokenService;
import hhp.concert.reservation.domain.entity.TokenEntity;
import hhp.concert.reservation.validate.TokenValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
public class TokenController {

//    @Autowired
//    private TokenService tokenService;
//
//    private TokenValidate tokenValidate;
//
//    // 대기열 토큰 발급
//    @PostMapping
//    public String issueToken(@RequestParam Long userSeq) {
//        return tokenService.issueToken(userSeq);
//    }
//
//    // 대기 순번 조회
//    @GetMapping("/queuePosition")
//    public int getQueuePosition(@RequestHeader("Authorization") String token) {
//        token = token.replace("Bearer ", "");
//        if (tokenValidate.validateToken(token)) {
//            return tokenService.getQueuePosition(token);
//        } else {
//            throw new RuntimeException("유효하지 않은 토큰입니다.");
//        }
//    }

    @Autowired
    private TokenService tokenService;

    @PostMapping("/generate")
    public TokenEntity generateToken(@RequestParam Long userId) {
        return tokenService.generateToken(userId);
    }

    @GetMapping("/queue/position")
    public int getQueuePosition(@RequestParam Long userId) {
        return tokenService.getQueuePosition(userId);
    }

    @GetMapping("/ready/position")
    public int getReadyQueuePosition(@RequestParam Long userId) {
        return tokenService.getReadyQueuePosition(userId);
    }

}
