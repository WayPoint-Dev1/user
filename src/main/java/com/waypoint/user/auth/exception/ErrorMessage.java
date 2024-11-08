package com.waypoint.user.auth.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {
  MANDATORY_FIELDS_MISSING(422, "MANDATORY_FIELDS_MISSING", "MANDATORY FIELDS MISSING IN PAYLOAD"),
  LOGIN_FAILED(422, "INCORRECT_LOGIN_CREDENTIALS", "INCORRECT USERNAME/PASSWORD"),
  USER_SIGNUP_FAILED(500, "USER_SIGNUP_FAILED", "ERROR WHILE PROCESSING SIGNUP REQUEST"),
  INVALID_OTP(422, "INVALID_OTP", "INCORRECT OTP"),
  FAILED_TO_SEND_OTP(500, "FAILED_TO_SEND_OTP", "FAILED TO SEND OTP"),
  INVALID_USERNAME(422, "INVALID_USERNAME", "PROVIDED USERNAME IS LINKED TO ANOTHER ACCOUNT"),
  INVALID_USERNAME_AND_EMAIL(422, "INVALID_USERNAME_AND_EMAIL", "PROVIDED USERNAME/EMAIL DOESN'T EXIST"),
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
