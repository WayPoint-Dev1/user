package com.waypoint.user.auth.service;

import static com.waypoint.user.auth.constants.AuthConstants.*;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.waypoint.user.auth.domain.dto.UserDTO;
import com.waypoint.user.auth.domain.entity.User;
import com.waypoint.user.auth.exception.AuthException;
import com.waypoint.user.auth.exception.ErrorMessage;
import com.waypoint.user.auth.repository.UserRepository;
import com.waypoint.user.auth.utilities.AuthMapper;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthService {
  private final UserRepository userRepository;
  private final EmailService emailService;
  private final KeyGenerator keyGenerator;
  private final TimeBasedOneTimePasswordGenerator totp;

  @Value("${hashing.key}")
  private String key;

  public AuthService(UserRepository userRepository, EmailService emailService) throws Exception {
    this.userRepository = userRepository;
    this.emailService = emailService;
    keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
    keyGenerator.init(TOTP_KEY_SIZE);
    totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(OTP_EXP_DURATION));
  }

  private String hashPassword(String password) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      mac.init(keySpec);
      byte[] hashedBytes = mac.doFinal(password.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hashedBytes);
    } catch (Exception e) {
      log.error("hashPassword :: Exception :: {}", e);
      throw new RuntimeException(e);
    }
  }

  public Mono<UserDTO> userSignin(UserDTO userDTO) {
    log.info("userSignin :: userDTO :: {}", userDTO);
    return getUserDetails(userDTO)
        .switchIfEmpty(Mono.error(new AuthException(ErrorMessage.INVALID_USERNAME_AND_EMAIL)))
        .flatMap(
            user -> {
              if (StringUtils.equals(
                  hashPassword(userDTO.getPassword()), user.getHashedPassword())) {
                // String token = jwtUtil.generateToken(userDTO.getUserId());
                return Mono.just(user);
              }
              return Mono.error(new AuthException(ErrorMessage.LOGIN_FAILED));
            })
        .map(AuthMapper::getUserDTO);
  }

  public Mono<UserDTO> userSignup(UserDTO userDTO) {
    log.info("userSignup :: userDTO :: {}", userDTO);
    userDTO.setPassword(hashPassword(userDTO.getPassword()));
    userDTO.setSecret(Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded()));
    return validateUserName(userDTO.getUserName())
        .then(
            userRepository
                .save(AuthMapper.getUser(userDTO))
                .switchIfEmpty(Mono.error(new AuthException(ErrorMessage.USER_SIGNUP_FAILED)))
                .map(AuthMapper::getUserDTO));
  }

  public Mono<String> validateUserName(String userName) {
    log.info("validateUserId :: userDTO :: {}", userName);
    return userRepository
        .existsByUserNameAndIsActive(userName, true)
        .flatMap(
            isPresent ->
                isPresent
                    ? Mono.error(new AuthException(ErrorMessage.INVALID_USERNAME))
                    : Mono.just(userName));
  }

  public Mono<UserDTO> resetPassword(UserDTO userDTO) {
    log.info("resetPassword :: userDTO :: {}", userDTO);

    return getUserDetails(userDTO)
        .switchIfEmpty(Mono.error(new AuthException(ErrorMessage.INVALID_USERNAME_AND_EMAIL)))
        .flatMap(
            user -> {
              if (!StringUtils.equals(
                  hashPassword(userDTO.getPassword()), user.getHashedPassword())) {
                user.setHashedPassword(hashPassword(userDTO.getPassword()));
                return Mono.just(user);
              }
              return Mono.error(new AuthException(ErrorMessage.INVALID_PASSWORD));
            })
        .flatMap(userRepository::save)
        .map(AuthMapper::getUserDTO);
  }

  public Mono<User> getUserDetails(UserDTO userDTO) {
    log.info("getUserDetails :: userDTO :: {}", userDTO);
    if (StringUtils.isNotBlank(userDTO.getUserName())) {
      return userRepository.findByUserNameAndIsActive(userDTO.getUserName(), true);
    } else if (StringUtils.isNotBlank(userDTO.getEmail())) {
      return userRepository.findByEmailAndIsActive(userDTO.getEmail(), true);
    }
    return Mono.error(new AuthException(ErrorMessage.MANDATORY_FIELDS_MISSING));
  }

  public Mono<UserDTO> sendOTP(UserDTO userDTO) {
    log.info("sendOTP :: userDTO :: {}", userDTO);
    SecretKey key = new SecretKeySpec(userDTO.getSecret().getBytes(), AES_ALGORITHM);
    try {
      String otp = totp.generateOneTimePasswordString(key, Instant.now());
      return emailService
          .sendOtpMail(userDTO.getEmail(), otp, userDTO.getFirstName())
          .thenReturn(userDTO);
    } catch (Exception e) {
      log.info("sendOTP :: Exception :: {}", e);
    }
    return Mono.error(new AuthException(ErrorMessage.FAILED_TO_SEND_OTP));
  }

  public Mono<UserDTO> validateOTP(UserDTO userDTO, String otp) {
    log.info("validateOTP :: userDTO :: {} :: otp :: {}", userDTO, otp);
    return getUserDetails(userDTO)
        .switchIfEmpty(Mono.error(new AuthException(ErrorMessage.INVALID_USERNAME_AND_EMAIL)))
        .map(AuthMapper::getUserDTO)
        .flatMap(
            details -> {
              SecretKey key = new SecretKeySpec(details.getSecret().getBytes(), AES_ALGORITHM);
              try {
                if (StringUtils.equals(
                    totp.generateOneTimePasswordString(key, Instant.now()), otp)) {
                  return Mono.just(details);
                }
              } catch (Exception e) {
                log.info("validateOTP :: Exception :: {}", e);
              }
              return Mono.error(new AuthException(ErrorMessage.INVALID_OTP));
            });
  }
}
