package com.waypoint.user.auth.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserManagementException extends RuntimeException {
  private final ErrorMessage errorMessage;

  @Builder
  public UserManagementException(ErrorMessage errorMessage) {
    super(errorMessage.getMessageDescription());
    this.errorMessage = errorMessage;
  }
}
