package hhp.concert.reservation.validate;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConcertValidate {

    // 콘서트 ID가 null인지 확인하는 메서드
    public void validateConcertId(boolean exists) {
        if (!exists) {
            throw new IllegalArgumentException("콘서트가 없습니다.");
        }
    }

    // 과거 날짜를 필터링하여 현재 날짜 이후의 날짜만 반환하는 메서드
    public List<LocalDate> filterPastDates(List<LocalDate> dates) {
        LocalDate today = LocalDate.now();
        return dates.stream()
                .filter(date -> !date.isBefore(today)) // 오늘 이후의 날짜만 포함
                .collect(Collectors.toList());
    }

}
