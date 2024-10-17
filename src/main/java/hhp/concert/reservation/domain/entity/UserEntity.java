package hhp.concert.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Entity
@Table(name = "userInfo")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column
    private String userName;

    @Column(nullable = false)
    private int pay;

    @OneToMany(mappedBy = "userEntity")
    private List<TokenEntity> tokens;

    @OneToMany(mappedBy = "userEntity")
    private List<ReservationEntity> reservationEntities;

    @OneToMany(mappedBy = "userEntity")
    private List<PaymentEntity> paymentEntities;

}
