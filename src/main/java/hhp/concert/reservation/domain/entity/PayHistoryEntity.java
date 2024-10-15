package hhp.concert.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class PayHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column
    private int amount;

    @Column
    private LocalDateTime paymentTime;

    @ManyToOne
    @JoinColumn(name = "user_seq", referencedColumnName = "userSeq")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "reservation_id", referencedColumnName = "reservationId")
    private ReservationEntity reservationEntity;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED
    }

}
