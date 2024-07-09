package com.travelguide.user.auth.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
  private final ErrorMessage errorMessage;

  @Builder
  public AuthException(ErrorMessage errorMessage) {
    super(errorMessage.getMessageDescription());
    this.errorMessage = errorMessage;
  }
}
