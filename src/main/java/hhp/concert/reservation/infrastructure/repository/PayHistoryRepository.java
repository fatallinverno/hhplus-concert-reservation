package hhp.concert.reservation.infrastructure.repository;

import hhp.concert.reservation.domain.entity.PayHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayHistoryRepository extends JpaRepository<PayHistoryEntity, Long> {
    List<PayHistoryEntity> findByPayerId(Long id);
}
