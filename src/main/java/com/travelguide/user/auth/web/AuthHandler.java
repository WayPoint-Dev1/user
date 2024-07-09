package com.travelguide.user.auth.web;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.travelguide.user.auth.domain.dto.UserDetails;
import com.travelguide.user.auth.service.AuthService;
import com.travelguide.user.auth.utilities.AuthMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AuthHandler implements HandlerFunction<ServerResponse> {
  private final AuthService authService;

  public AuthHandler(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public Mono<ServerResponse> handle(ServerRequest request) {
    return request
        .bodyToMono(UserDetails.class)
        .flatMap(AuthMapper::validateLoginRequest)
        .flatMap(authService::getUserDetails)
        .flatMap(
            tuple2 ->
                ok().contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tuple2.getT2())
                    .bodyValue(tuple2.getT1()));
  }

  public Mono<ServerResponse> handleCreateUser(ServerRequest request) {
    return request
        .bodyToMono(UserDetails.class)
        .flatMap(AuthMapper::validateCreateUserRequest)
        .flatMap(authService::createUser)
        .flatMap(
            userDetails -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(userDetails));
  }

  public Mono<ServerResponse> handleValidateUserId(ServerRequest request) {
    return request
        .bodyToMono(UserDetails.class)
        .flatMap(authService::validateUserId)
        .flatMap(
            userDetails -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(userDetails));
  }

  public Mono<ServerResponse> handleSendOTP(ServerRequest request) {
    return request
        .bodyToMono(UserDetails.class)
        .flatMap(authService::sendOTP)
        .flatMap(
            userDetails -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(userDetails));
  }

  public Mono<ServerResponse> handleValidateOTP(ServerRequest request) {
    return request
        .bodyToMono(UserDetails.class)
        .flatMap(authService::validateOTP)
        .flatMap(
            userDetails -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(userDetails));
  }

  public Mono<ServerResponse> handleResetPassword(ServerRequest request) {
    return request
        .bodyToMono(UserDetails.class)
        .flatMap(AuthMapper::validateResetPasswordRequest)
        .flatMap(authService::resetPassword)
        .flatMap(
            userDetails -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(userDetails));
  }
}
