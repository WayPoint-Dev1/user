package com.waypoint.user.auth.service;

import com.waypoint.user.auth.domain.dto.UserTripMapDTO;
import com.waypoint.user.auth.exception.ErrorMessage;
import com.waypoint.user.auth.exception.GenericException;
import com.waypoint.user.auth.repository.UserRepository;
import com.waypoint.user.auth.repository.UserTripMapRepository;
import com.waypoint.user.auth.utilities.UserMappingMapper;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserMappingService {
  private final UserTripMapRepository userTripMapRepository;
  private final UserRepository userRepository;

  public UserMappingService(
      UserTripMapRepository userTripMapRepository, UserRepository userRepository) {
    this.userTripMapRepository = userTripMapRepository;
    this.userRepository = userRepository;
  }

  public Mono<UserTripMapDTO> createUserTripMapping(String username, UUID tripId) {
    log.info("createUserTripMapping :: username :: {} :: tripId :: {}", username, tripId);
    return userRepository
        .findByUserNameAndIsActive(username, true)
        .switchIfEmpty(Mono.error(new GenericException(ErrorMessage.USERNAME_NOT_FOUND)))
        .flatMap(
            user ->
                userTripMapRepository
                    .findByUserIdAndTripIdAndIsActive(user.getId(), tripId, true)
                    .switchIfEmpty(
                        userTripMapRepository
                            .save(UserMappingMapper.getUserTripMap(user.getId(), tripId))
                            .flatMap(
                                userTripMap -> userTripMapRepository.findById(userTripMap.getId()))
                            .onErrorResume(
                                throwable ->
                                    Mono.error(
                                        new GenericException(
                                            ErrorMessage.USER_TRIP_MAPPING_FAILED))))
                    .map(UserMappingMapper::getUserTripMapDTO));
  }

  public Mono<UserTripMapDTO> deleteUserTripMapping(String username, UUID tripId) {
    log.info("deleteUserTripMapping :: username :: {} :: tripId :: {}", username, tripId);
    return userRepository
        .findByUserNameAndIsActive(username, true)
        .switchIfEmpty(Mono.error(new GenericException(ErrorMessage.USERNAME_NOT_FOUND)))
        .flatMap(
            user ->
                userTripMapRepository
                    .findByUserIdAndTripIdAndIsActive(user.getId(), tripId, true)
                    .switchIfEmpty(
                        Mono.error(new GenericException(ErrorMessage.USER_TRIP_MAPPING_NOT_FOUND)))
                    .flatMap(
                        userTripMap ->
                            userTripMapRepository
                                .save(UserMappingMapper.deleteUserTripMap(userTripMap))
                                .map(UserMappingMapper::getUserTripMapDTO)));
  }
}
