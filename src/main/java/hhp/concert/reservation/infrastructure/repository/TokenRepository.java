package hhp.concert.reservation.infrastructure.repository;

import hhp.concert.reservation.domain.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByUserEntityUserSeqAndStatus(Long userSeq, String status);

    Optional<TokenEntity> findByToken(String token);
}
