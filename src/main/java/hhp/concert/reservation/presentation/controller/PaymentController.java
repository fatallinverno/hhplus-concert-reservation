package hhp.concert.reservation.presentation.controller;

import hhp.concert.reservation.application.service.PaymentService;
import hhp.concert.reservation.domain.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payment")
    public PaymentEntity processPayment(@RequestParam Long userId, @RequestParam Long seatId, @RequestParam int amount, @RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", "");
        return paymentService.processPayment(userId, seatId, amount, token);
    }

}
