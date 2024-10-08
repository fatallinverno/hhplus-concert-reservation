### 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant TokenService
    participant ReservationService
    participant PayService
    participant PayHistoryService

    User ->> ReservationService: 예약 가능 날짜 조회 요청
    ReservationService ->> User: 예약 가능 날짜 목록 반환
    
    User ->> ReservationService: 예약 가능 좌석 조회 요청
    User ->> TokenService: 토큰 발급 요청
    TokenService ->> User: 토큰 발급 및 대기열 정보 반환
    ReservationService ->> User: 예약 가능 좌석 정보 반환

    User ->> ReservationService: 좌석 예약 요청 (토큰 포함)
    
    ReservationService ->> ReservationService: 좌석 임시 배정 (타이머 시작)
    ReservationService ->> User: 좌석 예약 확인 응답

    User ->> PayService: 잔액 조회 요청
    PayService ->> User: 잔액 반환

    User ->> PayService: 결제 요청 (잔액 확인, 토큰 포함)
    PayHistoryService ->> PayService: 잔액 차감 요청
    PayService ->> PayHistoryService: 잔액 차감 확인
    PayHistoryService ->> ReservationService: 좌석 최종 배정 요청
    ReservationService ->> PayHistoryService: 좌석 배정 완료
    PayHistoryService ->> User: 결제 완료 응답
    PayHistoryService ->> TokenService: 토큰 만료 처리
```

### 클래스 다이어그램
```mermaid
classDiagram
    class User {
        -long userId
        -String name
        -int pay
        +getPay()
        +chargePay(amount: int)
    }
    
    class Token {
        -UUID tokenId
        -long userId
        -DateTime issuedAt
        -int queuePosition
        -DateTime expirationTime
        +isValid(): boolean
        +expireToken()
    }
    
    class Reservation {
        -long reservationId
        -long userId
        -int seatNumber
        -Date reservationDate
        -DateTime expirationTime
        -boolean isTemporary
        +holdSeat(seatNumber: int)
        +releaseSeat()
    }
    
    class PayHistory {
        -long paymentId
        -long userId
        -long reservationId
        -int amount
        -DateTime paymentTime
        +processPayment(amount: int)
        +usageHistory()
    }
    
    class Seat {
        -int seatNumber
        -boolean isAvailable
        +checkAvailability(): boolean
        +reserveSeat()
        +freeSeat()
    }

    User "1" --> "0..*" Token
    User "1" --> "0..*" Reservation
    User "1" --> "0..*" Payment
    Reservation "1" --> "1" Seat
```

### 플로우 차트
```mermaid
flowchart TD
    A[서비스 시작] --> B[예약 가능 날짜 조회]
    B --> C[유저 토큰 발급 요청]
    C --> D{대기열 검증}
    D -->|성공| E[예약 가능 좌석 조회]
    E --> F[좌석 예약 요청]
    F --> G{잔액 확인}
    G -->|충전 필요| H[잔액 충전 요청]
    G -->|충분함| I[결제 요청]
    I --> J{좌석 임시 배정 여부}
    J -->|임시 배정 성공| K[좌석 최종 배정]
    K --> L[결제 완료]
    L --> M[토큰 만료 처리]
    M --> N[결제 성공 및 예약 완료]
    
    G -->|잔액 부족| H
    H --> I
    J -->|임시 배정 실패| X[예약 실패]
    L -->|결제 실패| X
```

### 유즈 케이스
![concert_drawio](https://github.com/user-attachments/assets/6a807252-7fbb-46e4-8fe5-9e00e9526b74)