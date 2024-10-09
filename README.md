### ERD 설계
```mermaid
User
------
- user_id (PK, LONG)
- name (VARCHAR)
- pay (INT)

Token
------
- token_id (PK, LONG)
- user_id (FK, LONG, REFERENCES User(user_id))
- queue_position (INT)
- issued_at (TIMESTAMP)
- expiration_time (TIMESTAMP)
- is_valid (BOOLEAN)

Reservation
------
- reservation_id (PK, LONG)
- user_id (FK, LONG, REFERENCES User(user_id))
- seat_id (FK, INT, REFERENCES Seat(seat_id))
- reservation_date (DATE)
- expiration_time (TIMESTAMP)
- is_temporary (BOOLEAN)

Payhistory
------
- pay_id (PK, LONG)
- user_id (FK, LONG, REFERENCES User(user_id))
- reservation_id (FK, LONG, REFERENCES Reservation(reservation_id))
- amount (DECIMAL)
- pay_time (TIMESTAMP)
- pay_status (ENUM: 'PENDING', 'COMPLETED', 'FAILED')

Seat
------
- seat_id (PK, INT)
- seat_number (INT, UNIQUE)
- is_available (BOOLEAN)
```