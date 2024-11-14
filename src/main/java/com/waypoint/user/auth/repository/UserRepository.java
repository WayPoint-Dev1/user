package com.waypoint.user.auth.repository;

import com.waypoint.user.auth.domain.entity.User;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
  Mono<Boolean> existsByUserNameAndIsActive(String userName, Boolean isActive);

  Mono<User> findByUserNameAndIsActive(String userId, Boolean isActive);

  Mono<User> findByUserNameAndEmailAndIsActive(String userId, String email, Boolean isActive);

  Mono<User> findByEmailAndIsActive(String userId, Boolean isActive);

  Mono<User> findByUserId(String userId);

  Mono<Integer> countByUserId(String userId);
}
