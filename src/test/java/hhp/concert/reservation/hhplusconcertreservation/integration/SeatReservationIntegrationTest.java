package hhp.concert.reservation.hhplusconcertreservation.integration;

import hhp.concert.reservation.application.service.ReservationService;
import hhp.concert.reservation.domain.entity.ConcertEntity;
import hhp.concert.reservation.domain.entity.SeatEntity;
import hhp.concert.reservation.domain.entity.UserEntity;
import hhp.concert.reservation.infrastructure.repository.ConcertRepository;
import hhp.concert.reservation.infrastructure.repository.ReservationRepository;
import hhp.concert.reservation.infrastructure.repository.SeatRepository;
import hhp.concert.reservation.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SeatReservationIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ReservationRepository reservationRepository; // ReservationRepository 추가

    private Long seatId;
    private Long userId;
    private Long concertId;

    @BeforeEach
    public void setUp() {
        // 테스트에 사용할 사용자 초기화
        UserEntity user = new UserEntity();
        user.setUserId(1L); // 사용자 ID 설정
        userRepository.save(user);
        userId = user.getUserId();

        // 콘서트 엔티티 초기화
        ConcertEntity concert = new ConcertEntity();
        concert.setConcertName("Test Concert"); // 콘서트 이름 설정
        concert.setConcertDate(LocalDate.now()); // 오늘 날짜로 설정
        concertRepository.save(concert);
        concertId = concert.getConcertId();

        // 좌석 엔티티 초기화
        SeatEntity seat = new SeatEntity();
        seat.setSeatNumber(1); // 좌석 번호 설정
        seat.setAvailable(true); // 좌석을 사용 가능으로 설정
        seat.setReservationId(null); // 초기화 시 예약 ID 없음
        seatRepository.save(seat);
        seatId = seat.getSeatId();
    }

    @Test
    public void testConcurrentReservation() throws InterruptedException {
        int userCount = 50; // 동시 예약 시도 사용자 수
        CountDownLatch latch = new CountDownLatch(userCount);
        ExecutorService executorService = Executors.newFixedThreadPool(userCount);

        for (int i = 0; i < userCount; i++) {
            executorService.execute(() -> {
                try {
                    reservationService.reserveSeat(userId, seatId); // 콘서트 ID도 필요 시 인자로 추가
                } catch (RuntimeException e) {
                    // 예약 실패 시 예외를 무시하고 카운트 다운
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 완료될 때까지 대기

        // 좌석이 예약된 경우 (1개 좌석이기 때문에 1개의 예약만 성공해야 함)
        long reservationCount = reservationRepository.count(); // 저장된 예약 수
        assertEquals(1, reservationCount, "좌석은 한 번만 예약되어야 합니다.");

        // 모든 예약이 완료된 후, 좌석의 예약 상태를 확인
        SeatEntity seat = seatRepository.findById(seatId).orElseThrow(() -> new RuntimeException("좌석을 찾을 수 없습니다."));
        assertEquals(false, seat.isAvailable(), "좌석이 예약되어야 하며 사용 불가능해야 합니다.");

        executorService.shutdown(); // ExecutorService 종료
    }

}
