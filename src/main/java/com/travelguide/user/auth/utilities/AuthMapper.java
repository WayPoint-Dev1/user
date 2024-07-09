package com.travelguide.user.auth.utilities;

import com.travelguide.user.auth.domain.User;
import com.travelguide.user.auth.domain.dto.UserDetails;
import com.travelguide.user.auth.domain.request.EmailRequest;
import com.travelguide.user.auth.exception.AuthException;
import com.travelguide.user.auth.exception.ErrorMessage;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

public class AuthMapper {

  public static UserDetails convertToUserDetails(User user) {
    return UserDetails.builder()
        .userId(user.getUserId())
        .firstName(user.getFirstName())
        .middleName(user.getMiddleName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .mobileNo(user.getMobileNo())
        .secret(user.getSecret())
        .mfa(user.isMfa())
        .build();
  }

  public static EmailRequest convertToEmailRequest(UserDetails userDetails) {
    return EmailRequest.builder()
        .subject("ONE TIME PASSWORD")
        .msgBody(userDetails.getOtp())
        .recipient(userDetails.getEmail())
        .build();
  }

  public static User convertToUser(UserDetails userDetails) {
    return User.builder()
        .userId(userDetails.getUserId())
        .email(userDetails.getEmail())
        .mobileNo(userDetails.getMobileNo())
        .firstName(userDetails.getFirstName())
        .middleName(userDetails.getMiddleName())
        .lastName(userDetails.getLastName())
        .password(userDetails.getPassword())
        .secret(userDetails.getSecret())
        .build();
  }

  public static Mono<UserDetails> validateCreateUserRequest(UserDetails userDetails) {
    return (!StringUtils.isEmpty(userDetails.getUserId())
            && !StringUtils.isEmpty(userDetails.getEmail())
            && !StringUtils.isEmpty(userDetails.getMobileNo())
            && !StringUtils.isEmpty(userDetails.getPassword())
            && (!StringUtils.isEmpty(userDetails.getFirstName())
                || !StringUtils.isEmpty(userDetails.getMiddleName())
                || !StringUtils.isEmpty(userDetails.getLastName())))
        ? Mono.just(userDetails)
        : Mono.error(new AuthException(ErrorMessage.MANDATORY_FIELDS_MISSING));
  }

  public static Mono<UserDetails> validateLoginRequest(UserDetails userDetails) {
    return (!StringUtils.isEmpty(userDetails.getUserId())
                || !StringUtils.isEmpty(userDetails.getEmail()))
            && !StringUtils.isEmpty(userDetails.getPassword())
        ? Mono.just(userDetails)
        : Mono.error(new AuthException(ErrorMessage.MANDATORY_FIELDS_MISSING));
  }

  public static Mono<UserDetails> validateResetPasswordRequest(UserDetails userDetails) {
    return ((!StringUtils.isEmpty(userDetails.getUserId())
                || !StringUtils.isEmpty(userDetails.getEmail()))
            && !StringUtils.isEmpty(userDetails.getPassword()))
        ? Mono.just(userDetails)
        : Mono.error(new AuthException(ErrorMessage.MANDATORY_FIELDS_MISSING));
  }
}
