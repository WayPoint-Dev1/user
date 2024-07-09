package com.travelguide.user.auth.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
  private final int code;
  private final String message;

  public ErrorResponse(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
