### ERD 설계
```mermaid
erDiagram
    User {
        LONG user_id PK
        VARCHAR name
        DECIMAL pay
    }

    Token {
        LONG token_id PK
        LONG user_id FK "REFERENCES User(user_id)"
        INT queue_position
        TIMESTAMP issued_at
        TIMESTAMP expiration_time
        BOOLEAN is_valid
    }

    Reservation {
        LONG reservation_id PK
        LONG user_id FK "REFERENCES User(user_id)"
        INT seat_id FK "REFERENCES Seat(seat_id)"
        DATE reservation_date
        TIMESTAMP expiration_time
        BOOLEAN is_temporary
    }

    PayHistory {
        LONG payment_id PK
        LONG user_id FK "REFERENCES User(user_id)"
        LONG reservation_id FK "REFERENCES Reservation(reservation_id)"
        DECIMAL amount
        TIMESTAMP payment_time
        ENUM payment_status "PENDING, COMPLETED, FAILED"
    }

    Seat {
        INT seat_id PK
        INT seat_number
        BOOLEAN is_available
    }

    User ||--o{ Token : "has"
    User ||--o{ Reservation : "makes"
    User ||--o{ PayHistory : "has"
    Reservation ||--o| Seat : "uses"
    Reservation ||--o{ PayHistory : "is associated with"
```