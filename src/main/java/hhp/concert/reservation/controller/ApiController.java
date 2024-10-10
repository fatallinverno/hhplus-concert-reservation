package hhp.concert.reservation.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private List<Map<String, Object>> users = new ArrayList<>();
    private List<Map<String, Object>> tokens = new ArrayList<>();
    private List<Map<String, Object>> reservations = new ArrayList<>();

    public ApiController() {
        users.add(Map.of("userSeq", 1L, "userId", "LeeKR", "pay", 100));
    }

    @PostMapping("/token")
    public Map<String, Object> generatorToken(@RequestBody Map<String, Object> request) {
        Long userSeq = Long.parseLong(request.get("userSeq").toString());
        Map<String, Object> token = Map.of(
                "tokenId", tokens.size() + 1L,
                "userSeq", userSeq,
                "queuePosition", new Random().nextInt(100),
                "expirationTime", new Date(System.currentTimeMillis() + 300000)
        );
        tokens.add(token);
        return token;
    }

    @GetMapping("/{userId}/pay")
    public Map<String, Object> getPay(@PathVariable Long userSeq) {
        return users.stream()
                .filter(user -> user.get("userSeq").equals(userSeq))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("유저가 없습니다."));
    }

    @PostMapping("/{userId}/pay")
    public Map<String, Object> chargePay(@PathVariable Long userSeq, @RequestBody Map<String, Object> request) {
        int amount = Integer.parseInt(request.get("amount").toString());
        Map<String, Object> user = getPay(userSeq);
        user.put("pay", (int) user.get("pay") + amount);
        return user;
    }

    @PostMapping("/reservation")
    public Map<String, Object> reserveSeat(@RequestBody Map<String, Object> request) {
        Map<String, Object> reservation = Map.of(
                "reservationId", reservations.size() + 1L,
                "userSeq", request.get("userSeq"),
                "seatId", request.get("seatId"),
                "expirationTime", new Date(System.currentTimeMillis() + 300000),
                "isTemporary", true
        );
        reservations.add(reservation);
        return reservation;
    }

    @PostMapping("/payment")
    public Map<String, Object> paymentProcess(@RequestBody Map<String, Object> request) {
        Long userSeq = Long.parseLong(request.get("userSeq").toString());
        int amount = Integer.parseInt(request.get("amount").toString());
        Map<String, Object> user = getPay(userSeq);

        if ((int) user.get("pay") < amount) {
            throw new RuntimeException("잔액이 부족합니다.");
        }

        user.put("pay", (int) user.get("pay") - amount);
        return Map.of(
                "userSeq", userSeq,
                "amount", amount,
                "status", "COMPLETED"
        );
    }

}
