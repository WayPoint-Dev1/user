package com.travelguide.user.auth.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
  MANDATORY_FIELDS_MISSING(422, "MANDATORY_FIELDS_MISSING", "MANDATORY FIELDS MISSING IN PAYLOAD"),
  LOGIN_FAILED(422, "INCORRECT_LOGIN_CREDENTIALS", "INCORRECT USERNAME/PASSWORD"),
  INVALID_OTP(422, "INVALID_OTP", "INCORRECT OTP"),
  INVALID_PASSWORD(422, "INVALID_PASSWORD", "NEW PASSWORD CANNOT BE SAME AS OLD PASSWORD"),
  UNAUTHORIZED(401, "UNAUTHORIZED", "UNAUTHORIZED");
  private final int httpStatusCode;
  private final String messageCode;
  private final String messageDescription;

  ErrorMessage(int httpStatusCode, String messageCode, String messageDescription) {
    this.httpStatusCode = httpStatusCode;
    this.messageCode = messageCode;
    this.messageDescription = messageDescription;
  }
}
