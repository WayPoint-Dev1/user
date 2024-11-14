package com.waypoint.user.auth.repository;

import com.waypoint.user.auth.domain.entity.UserTripMap;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserTripMapRepository extends ReactiveCrudRepository<UserTripMap, UUID> {
  Mono<UserTripMap> findByUserIdAndTripIdAndIsActive(UUID userId, UUID tripId, Boolean isActive);
}
