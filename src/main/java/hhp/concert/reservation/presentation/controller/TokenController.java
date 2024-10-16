package hhp.concert.reservation.presentation.controller;

import hhp.concert.reservation.application.service.TokenService;
import hhp.concert.reservation.domain.entity.TokenEntity;
import hhp.concert.reservation.validate.TokenValidate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("/generate")
    public TokenEntity generateToken(@RequestParam Long userSeq) {
        return tokenService.generateToken(userSeq);
    }

    @GetMapping("/queue/position")
    public int getQueuePosition(@RequestParam Long userSeq) {
        return tokenService.getQueuePosition(userSeq);
    }

}
