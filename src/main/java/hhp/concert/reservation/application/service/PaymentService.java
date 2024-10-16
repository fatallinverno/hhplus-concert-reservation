package hhp.concert.reservation.application.service;

import hhp.concert.reservation.domain.entity.PaymentEntity;
import hhp.concert.reservation.domain.entity.SeatEntity;
import hhp.concert.reservation.domain.entity.TokenEntity;
import hhp.concert.reservation.domain.entity.UserEntity;
import hhp.concert.reservation.infrastructure.repository.PaymentRepository;
import hhp.concert.reservation.infrastructure.repository.SeatRepository;
import hhp.concert.reservation.infrastructure.repository.TokenRepository;
import hhp.concert.reservation.infrastructure.repository.UserRepository;
import hhp.concert.reservation.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public PaymentEntity processPayment(Long userSeq, Long seatId, int amount, String token) {
        UserEntity user = userRepository.findById(userSeq)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        SeatEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("좌석을 찾을 수 없습니다."));

        PaymentEntity payment = new PaymentEntity();
        payment.setUser(user);
        payment.setSeat(seat);
        payment.setAmount(amount);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentStatus(PaymentEntity.PaymentStatus.COMPLETED);

        paymentRepository.save(payment);

        seat.setAvailable(false);
        seatRepository.save(seat);

        TokenEntity tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

        tokenService.completeToken(tokenEntity.getTokenId());

        return payment;
    }

}
