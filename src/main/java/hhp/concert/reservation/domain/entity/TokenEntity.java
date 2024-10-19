package hhp.concert.reservation.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
@Getter
@Setter
@Table(name = "token")
public class TokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userSeq")
    private UserEntity userEntity;

    private int queuePosition;

    private LocalDateTime issuedAt;

    private LocalDateTime expirationTime;

    private boolean isValid;
}
