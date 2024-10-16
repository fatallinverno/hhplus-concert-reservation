package hhp.concert.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payHistory")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column
    private int amount;

    @Column
    private LocalDateTime paymentTime;

    @ManyToOne
    @JoinColumn(name = "user_seq", referencedColumnName = "userSeq")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "seat_id", referencedColumnName = "seatId")
    private SeatEntity seat;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED
    }

}
