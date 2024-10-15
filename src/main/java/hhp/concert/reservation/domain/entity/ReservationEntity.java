package hhp.concert.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Getter
@Setter
@Table(name = "reserve")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column
    private LocalDate reservationDate;

    @Column
    private LocalDateTime expirationTime;

    @Column
    private boolean isTemporary;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userSeq")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "seat_id", referencedColumnName = "seatId")
    private SeatEntity seatEntity;

}
