package hhp.concert.reservation.infrastructure.repository;

import hhp.concert.reservation.domain.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

}
