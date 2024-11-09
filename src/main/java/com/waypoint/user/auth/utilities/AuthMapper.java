package com.waypoint.user.auth.utilities;

import static com.waypoint.user.auth.constants.AuthConstants.DEF_USER_NAME;
import static com.waypoint.user.auth.constants.AuthConstants.OTP_EMAIL_SUBJECT;

import com.waypoint.user.auth.domain.dto.UserDTO;
import com.waypoint.user.auth.domain.entity.User;
import com.waypoint.user.auth.exception.AuthException;
import com.waypoint.user.auth.exception.ErrorMessage;
import java.time.LocalDateTime;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.ses.model.*;

public class AuthMapper {

  private static final Logger log = LoggerFactory.getLogger(AuthMapper.class);

  public static UserDTO getUserDTO(User user) {
    return UserDTO.builder()
        .userName(user.getUserName())
        .firstName(user.getFirstName())
        .middleName(user.getMiddleName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .countryCode(user.getCountryCode())
        .mobileNo(user.getMobileNo())
        .secret(user.getSecret())
        .mfaEnabled(user.isMfaEnabled())
        .emailVerified(user.isEmailVerified())
        .build();
  }

  public static SendEmailRequest getSendEmailRequest(
      String senderEmail, String recipientEmail, String body) {
    return SendEmailRequest.builder()
        .destination(Destination.builder().toAddresses(recipientEmail).build())
        .message(
            Message.builder()
                .subject(Content.builder().data(OTP_EMAIL_SUBJECT).build())
                .body(Body.builder().html(Content.builder().data(body).build()).build())
                .build())
        .source(senderEmail)
        .build();
  }

  public static User getUser(UserDTO userDTO) {
    return User.builder()
        .userName(userDTO.getUserName())
        .email(userDTO.getEmail())
        .firstName(userDTO.getFirstName())
        .middleName(userDTO.getMiddleName())
        .lastName(userDTO.getLastName())
        .countryCode(userDTO.getCountryCode())
        .mobileNo(userDTO.getMobileNo())
        .hashedPassword(userDTO.getPassword())
        .secret(userDTO.getSecret())
        .emailVerified(false)
        .mfaEnabled(userDTO.isMfaEnabled())
        .isActive(true)
        .createdBy(DEF_USER_NAME)
        .createdOn(LocalDateTime.now())
        .updatedBy(DEF_USER_NAME)
        .updatedOn(LocalDateTime.now())
        .build();
  }

  public static Mono<UserDTO> validateUserSignupRequest(UserDTO userDTO) {
    log.info("validateUserSignupRequest :: {}", userDTO);
    return (!StringUtils.isEmpty(userDTO.getUserName())
            && !StringUtils.isEmpty(userDTO.getEmail())
            && !StringUtils.isEmpty(userDTO.getPassword())
            && !StringUtils.isEmpty(userDTO.getFirstName()))
        ? Mono.just(userDTO)
        : Mono.error(new AuthException(ErrorMessage.MANDATORY_FIELDS_MISSING));
  }

  public static Mono<UserDTO> validateOtpRequest(UserDTO userDTO) {
    log.info("validateSendOtpRequest :: {}", userDTO);
    return ((!StringUtils.isEmpty(userDTO.getUserName())
                || !StringUtils.isEmpty(userDTO.getEmail()))
            && !StringUtils.isEmpty(userDTO.getPassword()))
        ? Mono.just(userDTO)
        : Mono.error(new AuthException(ErrorMessage.MANDATORY_FIELDS_MISSING));
  }

  public static Mono<String> validateUserNameRequest(String userName) {
    log.info("validateUserName :: {}", userName);
    return !StringUtils.isEmpty(userName)
        ? Mono.just(userName)
        : Mono.error(new AuthException(ErrorMessage.MANDATORY_FIELDS_MISSING));
  }

  public static Mono<UserDTO> validateCheckOtpRequest(UserDTO userDTO) {
    log.info("validateCheckOtpRequest :: {}", userDTO);
    return (!StringUtils.isEmpty(userDTO.getUserName()) || !StringUtils.isEmpty(userDTO.getEmail()))
        ? Mono.just(userDTO)
        : Mono.error(new AuthException(ErrorMessage.MANDATORY_FIELDS_MISSING));
  }

  public static Mono<UserDTO> validateUserSigninRequest(UserDTO userDTO) {
    return ((!StringUtils.isEmpty(userDTO.getUserName())
                || !StringUtils.isEmpty(userDTO.getEmail()))
            && !StringUtils.isEmpty(userDTO.getPassword()))
        ? Mono.just(userDTO)
        : Mono.error(new AuthException(ErrorMessage.MANDATORY_FIELDS_MISSING));
  }

  public static Mono<UserDTO> validateResetPasswordRequest(UserDTO userDTO) {
    return ((!StringUtils.isEmpty(userDTO.getUserName())
                || !StringUtils.isEmpty(userDTO.getEmail()))
            && !StringUtils.isEmpty(userDTO.getPassword()))
        ? Mono.just(userDTO)
        : Mono.error(new AuthException(ErrorMessage.MANDATORY_FIELDS_MISSING));
  }
}
