package hhp.concert.reservation.infrastructure.repository;

import hhp.concert.reservation.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
