package hhp.concert.reservation.presentation.controller;


import hhp.concert.reservation.application.service.PayService;
import hhp.concert.reservation.domain.entity.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Api(tags = "결제 관리", description = "잔액 조회 및 충전 API")
public class PayController {

    @Autowired
    private PayService payService;

    // 잔액 조회 엔드포인트
    @GetMapping("/{userId}/pay")
    @ApiOperation(value = "잔액 조회", notes = "특정 사용자 ID의 잔액을 조회합니다.")
    public int getPay(@PathVariable Long userId) {
        return payService.getPay(userId);
    }

    // 잔액 충전 엔드포인트
    @PostMapping("/{userId}/chargePay")
    @ApiOperation(value = "잔액 충전", notes = "특정 사용자 ID에 잔액을 충전합니다.")
    public UserEntity chargePay(@PathVariable Long userId, @RequestParam int amount) {
        return payService.chargePay(userId, amount);
    }

}
