package com.travelguide.user.auth.constants;

public class AuthConstants {
  public static final String AES_ALGORITHM = "AES";
  public static final int TOTP_KEY_SIZE = 256;
  public static final long OTP_EXP_DURATION = 30;
  public static final String USER_LOGIN_URI = "/user/login";
  public static final String USER_CREATE_URI = "/user/create";
  public static final String VALIDATE_USER_ID_URI = "/val/uid";
  public static final String SEND_OTP_URI = "/send/otp";
  public static final String VALIDATE_OTP_URI = "/val/otp";
  public static final String RESET_PASSWORD_URI = "/reset/password";
  public static final String NOTIFY_USER_URI = "/send/email";
}
