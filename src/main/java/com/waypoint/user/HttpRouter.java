package com.waypoint.user;

import static com.waypoint.user.auth.constants.AuthConstants.*;
import static com.waypoint.user.auth.constants.UserMappingConstants.CREATE_USER_TRIP_MAPPING_URI;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.waypoint.user.auth.web.AuthHandler;
import com.waypoint.user.auth.web.UserMappingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HttpRouter {
  private final AuthHandler authHandler;
  private final UserMappingHandler userMappingHandler;

  public HttpRouter(AuthHandler authHandler, UserMappingHandler userMappingHandler) {
    this.authHandler = authHandler;
    this.userMappingHandler = userMappingHandler;
  }

  @Bean
  RouterFunction<ServerResponse> routes() {
    return route()
        .POST(USER_SIGNIN_URI, RequestPredicates.accept(MediaType.APPLICATION_JSON), authHandler)
        .POST(
            USER_SIGNUP_URI,
            RequestPredicates.accept(MediaType.APPLICATION_JSON),
            authHandler::handleUserSignup)
        .GET(
            VALIDATE_USER_NAME_URI,
            RequestPredicates.accept(MediaType.APPLICATION_JSON),
            authHandler::handleValidateUserName)
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
        .POST(
            CREATE_USER_TRIP_MAPPING_URI,
            RequestPredicates.accept(MediaType.APPLICATION_JSON),
            userMappingHandler)
        .build();
  }
}
