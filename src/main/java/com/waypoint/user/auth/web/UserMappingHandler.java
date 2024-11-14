package com.waypoint.user.auth.web;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.waypoint.user.auth.service.UserMappingService;
import com.waypoint.user.auth.utilities.UserMappingMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserMappingHandler implements HandlerFunction<ServerResponse> {
  private final UserMappingService userMappingService;

  public UserMappingHandler(UserMappingService userMappingService) {
    this.userMappingService = userMappingService;
  }

  @Override
  public Mono<ServerResponse> handle(ServerRequest request) {

    return UserMappingMapper.validateCreateUserTripMappingRequest(request)
        .flatMap(tuple2 -> userMappingService.createUserTripMapping(tuple2.getT1(), tuple2.getT2()))
        .flatMap(userDTO -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(userDTO));
  }
}
