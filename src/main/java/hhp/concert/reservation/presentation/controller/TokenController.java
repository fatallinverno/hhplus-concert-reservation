package hhp.concert.reservation.presentation.controller;

import hhp.concert.reservation.application.service.TokenService;
import hhp.concert.reservation.domain.entity.TokenEntity;
import hhp.concert.reservation.validate.TokenValidate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
@Tag(name = "Token Controller", description = "토큰 API")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("/generate")
    @Operation(summary = "토큰 발급", description = "토큰을 발급합니다.")
    public TokenEntity generateToken(@RequestParam Long userId) {
        return tokenService.generateToken(userId);
    }

    @GetMapping("/queue/position")
    @Operation(summary = "순번 조회", description = "대기열 순번 조회합니다.")
    public int getQueuePosition(@RequestParam Long userId) {
        return tokenService.getQueuePosition(userId);
    }

}
