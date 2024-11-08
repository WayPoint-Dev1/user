package com.waypoint.user.auth.web;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.waypoint.user.auth.domain.dto.UserDTO;
import com.waypoint.user.auth.service.AuthService;
import com.waypoint.user.auth.utilities.AuthMapper;
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
        .bodyToMono(UserDTO.class)
        .flatMap(AuthMapper::validateUserSigninRequest)
        .flatMap(authService::userSignin)
        .flatMap(userDTO -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(userDTO));
  }

  public Mono<ServerResponse> handleUserSignup(ServerRequest request) {
    return request
        .bodyToMono(UserDTO.class)
        .flatMap(AuthMapper::validateUserSignupRequest)
        .flatMap(authService::validateUserName)
        .flatMap(authService::userSignup)
        .flatMap(userDTO -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(userDTO));
  }

  public Mono<ServerResponse> handleValidateUserName(ServerRequest request) {
    return request
        .bodyToMono(UserDTO.class)
            .flatMap(AuthMapper::validateUserNameRequest)
        .flatMap(authService::validateUserName)
        .flatMap(userDTO -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(userDTO));
  }

  public Mono<ServerResponse> handleSendOTP(ServerRequest request) {
    return request
        .bodyToMono(UserDTO.class)
        .flatMap(AuthMapper::validateOtpRequest)
        .flatMap(authService::userSignin)
        .flatMap(authService::sendOTP)
        .flatMap(userDTO -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(userDTO));
  }

  public Mono<ServerResponse> handleValidateOTP(ServerRequest request) {
    return request
        .bodyToMono(UserDTO.class)
        .flatMap(AuthMapper::validateCheckOtpRequest)
        .flatMap(userDTO -> authService.validateOTP(userDTO, request.pathVariable("otp")))
        .flatMap(userDTO -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(userDTO));
  }

  public Mono<ServerResponse> handleResetPassword(ServerRequest request) {
    return request
        .bodyToMono(UserDTO.class)
        .flatMap(AuthMapper::validateResetPasswordRequest)
        .flatMap(authService::resetPassword)
        .flatMap(userDTO -> ok().contentType(MediaType.APPLICATION_JSON).bodyValue(userDTO));
  }
}
