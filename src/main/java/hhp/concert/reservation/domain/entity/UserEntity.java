package hhp.concert.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userSeq;

    @Column
    private String userId;

    @Column
    private int pay;

    @OneToMany(mappedBy = "userEntity")
    private List<TokenEntity> tokens;

    @OneToMany(mappedBy = "userEntity")
    private List<ReservationEntity> reservationEntities;

    @OneToMany(mappedBy = "userEntity")
    private List<PayHistoryEntity> payHistories;

    public UserEntity() {}

    public UserEntity(Long userSeq) {
        this.userSeq = userSeq;
    }

}
