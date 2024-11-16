package com.waypoint.user.auth.exception;

import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class UserManagementExceptionHandler {

  @ExceptionHandler(GenericException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleGenericException(GenericException ex) {
    return Mono.just(
        ResponseEntity.status(ex.getErrorMessage().getHttpStatusCode())
            .body(
                ErrorResponse.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .status(ex.getErrorMessage().getHttpStatusCode())
                    .error(ex.getErrorMessage().getMessageCode())
                    .message(ex.getErrorMessage().getMessageDescription())
                    .build()));
  }
}
