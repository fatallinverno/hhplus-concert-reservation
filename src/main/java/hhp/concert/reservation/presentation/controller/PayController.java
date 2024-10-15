package hhp.concert.reservation.presentation.controller;


import hhp.concert.reservation.application.service.PayService;
import hhp.concert.reservation.domain.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PayController {

    @Autowired
    private PayService payService;

    // 잔액 조회 엔드포인트
    @GetMapping("/{userId}/pay")
    public int getPay(@PathVariable Long userId) {
        return payService.getPay(userId);
    }

    // 잔액 충전 엔드포인트
    @PostMapping("/{userId}/chargePay")
    public UserEntity chargePay(@PathVariable Long userId, @RequestParam int amount) {
        return payService.chargePay(userId, amount);
    }

}
