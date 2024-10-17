package hhp.concert.reservation.presentation.controller;


import hhp.concert.reservation.application.service.PayService;
import hhp.concert.reservation.domain.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Pay Controller", description = "잔액 관리 API")
public class PayController {

    @Autowired
    private PayService payService;

    @GetMapping("/{userId}/pay")
    @Operation(summary = "잔액 조회", description = "유저의 잔액을 조회합니다.")
    public int getPay(@Parameter(description = "조회할 유저의 ID") @PathVariable Long userSeq) {
        return payService.getPay(userSeq);
    }

    @PostMapping("/{userId}/chargePay")
    @Operation(summary = "잔액 충전", description = "유저의 잔액을 충전합니다.")
    public UserEntity chargePay(@Parameter(description = "잔액을 충전할 유저의 ID") @PathVariable Long userSeq, @Parameter(description = "충전할 금액") @RequestParam int amount) {
        return payService.chargePay(userSeq, amount);
    }

}
