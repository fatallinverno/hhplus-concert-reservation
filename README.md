### 마일스톤
https://github.com/users/fatallinverno/projects/3

### 전체 시퀀스 다이어그램
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

### 유저 토큰 API 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant TokenService
    participant QueueManager
    
    User ->> TokenService: 토큰 발급 요청
    TokenService ->> QueueManager: 사용자 대기열에 추가
    QueueManager ->> TokenService: 대기열 정보 반환 (대기 순서, 예상 시간 등)
    TokenService ->> User: 토큰 및 대기열 정보 반환
```

### 예약 날짜 조회 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant ReservationService
    participant Database
    
    User ->> ReservationService: 예약 가능 날짜 조회 요청
    ReservationService ->> Database: 예약 가능한 날짜 데이터 요청
    Database ->> ReservationService: 예약 가능 날짜 데이터 반환
    ReservationService ->> User: 예약 가능 날짜 목록 반환
```

### 예약 가능 좌석 조회 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant ReservationService
    participant Database
    
    User ->> ReservationService: 예약 가능 좌석 조회 요청 (날짜 포함)
    ReservationService ->> Database: 해당 날짜의 좌석 데이터 요청
    Database ->> ReservationService: 좌석 상태 데이터 반환
    ReservationService ->> User: 예약 가능 좌석 목록 반환
```

### 좌석 예약 요청 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant TokenService
    participant ReservationService
    participant Database

    User ->> TokenService: 토큰 검증 요청
    TokenService ->> User: 토큰 유효성 확인 응답
    User ->> ReservationService: 좌석 예약 요청 (날짜 및 좌석 번호 포함)
    ReservationService ->> Database: 좌석 상태 확인 및 임시 예약 처리
    Database ->> ReservationService: 임시 예약 성공 여부 반환
    ReservationService ->> User: 임시 예약 성공 응답 및 타이머 시작 (예: 5분)
```

### 잔액 조회 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant PayService
    participant Database

    User ->> PayService: 잔액 조회 요청
    PayService ->> Database: 사용자 잔액 정보 요청
    Database ->> PayService: 사용자 잔액 정보 반환
    PayService ->> User: 잔액 조회 결과 반환
```

### 잔액 충전 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant PayService
    participant Database

    User ->> PayService: 잔액 충전 요청 (충전 금액 포함)
    PayService ->> Database: 잔액 업데이트 요청
    Database ->> PayService: 잔액 충전 완료 응답
    PayService ->> User: 충전 완료 응답
```

### 결제 요청 시퀀스 다이어그램
```mermaid
sequenceDiagram
    participant User
    participant TokenService
    participant PayHistoryService
    participant PayService
    participant ReservationService
    participant Database

    User ->> TokenService: 토큰 검증 요청
    TokenService ->> User: 토큰 유효성 확인 응답

    User ->> PayHistoryService: 결제 요청 (예약 ID, 금액 포함)
    PayHistoryService ->> PayService: 잔액 확인 요청
    PayService ->> Database: 사용자 잔액 조회
    Database ->> PayService: 사용자 잔액 정보 반환
    PayService ->> PayHistoryService: 잔액 확인 응답

    PayHistoryService ->> PayService: 잔액 차감 요청
    PayService ->> Database: 잔액 업데이트
    Database ->> PayService: 업데이트 결과 반환

    PayHistoryService ->> ReservationService: 최종 좌석 예약 요청
    ReservationService ->> Database: 좌석 최종 배정 처리
    Database ->> ReservationService: 배정 완료 응답

    PayHistoryService ->> TokenService: 토큰 만료 처리
    TokenService ->> Database: 토큰 상태 업데이트
    Database ->> TokenService: 업데이트 결과 반환

    PayHistoryService ->> User: 결제 완료 응답
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
    User "1" --> "0..*" PayHistory
    Reservation "1" --> "1" Seat
```

### 플로우 차트
```mermaid
flowchart TD
    A[서비스 시작] --> B[유저 토큰 발급 요청]
    B --> C[예약 가능 날짜 조회]
    C -->|성공| D[예약 가능 좌석 조회]
    D --> E{대기열 검증}
    E --> F[좌석 예약 요청]
    F --> G{좌석 임시 배정 여부}
    G -->|충분함| H[결제 요청]
    H --> I{잔액 확인}
    J -->|충전 필요| H[잔액 충전 요청]
    J -->|임시 배정 성공| K[좌석 최종 배정]
    K --> L[결제 완료]
    L --> M[토큰 만료 처리]
    M --> N[결제 성공 및 예약 완료]
    
    G -->|잔액 부족| H
    H --> I
    G -->|임시 배정 실패| X[예약 실패]
    L -->|결제 실패| X
```

### 유즈 케이스
![concert_drawio](https://github.com/user-attachments/assets/6a807252-7fbb-46e4-8fe5-9e00e9526b74)