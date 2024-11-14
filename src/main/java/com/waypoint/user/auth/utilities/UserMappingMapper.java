package com.waypoint.user.auth.utilities;

import static com.waypoint.user.auth.constants.AuthConstants.DEF_USER_NAME;

import com.waypoint.user.auth.domain.dto.UserTripMapDTO;
import com.waypoint.user.auth.domain.entity.UserTripMap;
import com.waypoint.user.auth.exception.ErrorMessage;
import com.waypoint.user.auth.exception.UserManagementException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Slf4j
public class UserMappingMapper {

  public static Mono<Tuple2<String, UUID>> validateCreateUserTripMappingRequest(
      ServerRequest serverRequest) {
    String username = serverRequest.pathVariable("username");
    String tripId = serverRequest.pathVariable("tripId");
    log.info(
        "validateCreateUserTripMappingRequest :: username :: {} :: tripId :: {}", username, tripId);
    if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(tripId)) {
      return Mono.just(Tuples.of(username, UUID.fromString(tripId)));
    }
    return Mono.error(new UserManagementException(ErrorMessage.MANDATORY_FIELDS_MISSING));
  }

  public static UserTripMap getUserTripMap(UUID userId, UUID tripId) {
    return UserTripMap.builder()
        .userId(userId)
        .tripId(tripId)
        .isActive(true)
        .createdBy(DEF_USER_NAME)
        .createdOn(LocalDateTime.now())
        .updatedBy(DEF_USER_NAME)
        .updatedOn(LocalDateTime.now())
        .build();
  }

  public static UserTripMapDTO getUserTripMapDTO(UserTripMap userTripMap) {
    return UserTripMapDTO.builder()
        .id(userTripMap.getId())
        .userTripMapId(userTripMap.getUserTripMapId())
        .userId(userTripMap.getUserId())
        .tripId(userTripMap.getTripId())
        .isActive(userTripMap.getIsActive())
        .createdBy(userTripMap.getCreatedBy())
        .createdOn(userTripMap.getCreatedOn())
        .updatedBy(userTripMap.getUpdatedBy())
        .updatedOn(userTripMap.getUpdatedOn())
        .build();
  }
}
