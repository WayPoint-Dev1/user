package com.waypoint.user.auth.repository;

import com.waypoint.user.auth.domain.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {
  Mono<Boolean> existsByUserNameAndIsActive(String userName, Boolean isActive);
  Mono<User> findByUserNameAndIsActive(String userId, Boolean isActive);
  Mono<User> findByEmailAndIsActive(String userId, Boolean isActive);

  Mono<User> findByUserId(String userId);

  Mono<Integer> countByUserId(String userId);
}
