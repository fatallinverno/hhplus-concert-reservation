package hhp.concert.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
@Getter
@Table(name = "seat")
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @Column(nullable = false, unique = true)
    private int seatNumber;

    @Column
    private boolean isAvailable;

    public SeatEntity() {}

    public SeatEntity(Long seatId) {
        this.seatId = seatId;
    }

    public SeatEntity(long l, int i, boolean b) {
    }
}
