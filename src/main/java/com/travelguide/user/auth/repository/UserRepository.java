package com.travelguide.user.auth.repository;

import com.travelguide.user.auth.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {
  Mono<User> findByUserId(String userId);

  Mono<Integer> countByUserId(String userId);
}
