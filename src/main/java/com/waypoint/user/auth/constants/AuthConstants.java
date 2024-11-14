package com.waypoint.user.auth.constants;

public class AuthConstants {
  public static final String AES_ALGORITHM = "AES";
  public static final int TOTP_KEY_SIZE = 256;
  public static final long OTP_EXP_DURATION = 60;
  public static final String USER_SIGNIN_URI = "/signin";
  public static final String USER_SIGNUP_URI = "/signup";
  public static final String VALIDATE_USER_NAME_URI = "/validate/username/{username}";
  public static final String SEND_OTP_URI = "/send/otp";
  public static final String VALIDATE_OTP_URI = "/validate/otp/{otp}";
  public static final String RESET_PASSWORD_URI = "/reset/password";
  public static final String DEF_USER_NAME = "user";
  public static final String OTP_EMAIL_SUBJECT = "Your OTP Code";
}
