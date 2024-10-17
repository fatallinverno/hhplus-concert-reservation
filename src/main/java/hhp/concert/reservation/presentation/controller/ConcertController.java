package hhp.concert.reservation.presentation.controller;


import hhp.concert.reservation.application.service.ConcertService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/concert")
public class ConcertController {

    @Autowired
    private ConcertService concertService;

    @GetMapping("/availableDates")
    @Operation(summary = "날짜 조회", description = "콘서트 날짜를 조회 합니다.")
    public List<LocalDate> getAvailableDates(@RequestParam Long concertId) {
        return concertService.findAvailableDatesByConcert(concertId);
    }

    @GetMapping("/availableSeats")
    @Operation(summary = "좌석 조회", description = "콘서트 날짜별 좌석를 조회 합니다.")
    public List<Integer> getAvailableSeats(@RequestParam Long concertId, @RequestParam String date) {
        return concertService.getAvailableSeats(concertId, date);
    }

}
