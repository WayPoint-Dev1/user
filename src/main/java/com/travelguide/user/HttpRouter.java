package com.travelguide.user;

import static com.travelguide.user.auth.constants.AuthConstants.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.travelguide.user.auth.web.AuthHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HttpRouter {
  private final AuthHandler authHandler;

  public HttpRouter(AuthHandler authHandler) {
    this.authHandler = authHandler;
  }

  @Bean
  RouterFunction<ServerResponse> routes() {
    return route()
        .POST(USER_LOGIN_URI, RequestPredicates.accept(MediaType.APPLICATION_JSON), authHandler)
        .POST(
            USER_CREATE_URI,
            RequestPredicates.accept(MediaType.APPLICATION_JSON),
            authHandler::handleCreateUser)
        .POST(
            VALIDATE_USER_ID_URI,
            RequestPredicates.accept(MediaType.APPLICATION_JSON),
            authHandler::handleValidateUserId)
        .POST(
            SEND_OTP_URI,
            RequestPredicates.accept(MediaType.APPLICATION_JSON),
            authHandler::handleSendOTP)
        .POST(
            VALIDATE_OTP_URI,
            RequestPredicates.accept(MediaType.APPLICATION_JSON),
            authHandler::handleValidateOTP)
        .POST(
            RESET_PASSWORD_URI,
            RequestPredicates.accept(MediaType.APPLICATION_JSON),
            authHandler::handleResetPassword)
        .build();
  }
}
