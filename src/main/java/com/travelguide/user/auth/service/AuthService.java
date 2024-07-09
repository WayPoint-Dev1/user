package com.travelguide.user.auth.service;

import static com.travelguide.user.auth.constants.AuthConstants.*;
import static com.travelguide.user.auth.utilities.AuthMapper.convertToEmailRequest;
import static com.travelguide.user.auth.utilities.AuthMapper.convertToUser;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.travelguide.user.auth.client.MessagingClient;
import com.travelguide.user.auth.constants.Status;
import com.travelguide.user.auth.domain.dto.UserDetails;
import com.travelguide.user.auth.exception.AuthException;
import com.travelguide.user.auth.exception.ErrorMessage;
import com.travelguide.user.auth.repository.UserRepository;
import com.travelguide.user.auth.utilities.AuthMapper;
import com.travelguide.user.auth.utilities.JwtUtil;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
@Slf4j
public class AuthService {
  private final UserRepository userRepository;
  private final MessagingClient messagingClient;
  private final JwtUtil jwtUtil;
  private final KeyGenerator keyGenerator;
  private final TimeBasedOneTimePasswordGenerator totp;

  @Value("${specialkey.cipher}")
  private String SPECIAL_KEY;

  public AuthService(
      UserRepository userRepository, MessagingClient messagingClient, JwtUtil jwtUtil)
      throws Exception {
    this.userRepository = userRepository;
    this.messagingClient = messagingClient;
    this.jwtUtil = jwtUtil;
    keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
    keyGenerator.init(TOTP_KEY_SIZE);
    totp = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(OTP_EXP_DURATION));
  }

  public static String encrypt(String data, String key) {
    try {
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), AES_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec);
      return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }

  public static String decrypt(byte[] encryptedData, String key) {
    try {
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), AES_ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, keySpec);
      return new String(cipher.doFinal(encryptedData));
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }

  public Mono<Tuple2<UserDetails, String>> getUserDetails(UserDetails userDetails) {
    return userRepository
        .findByUserId(userDetails.getUserId())
        .flatMap(
            user -> {
              if (StringUtils.equals(
                  userDetails.getPassword(),
                  decrypt(Base64.getDecoder().decode(user.getPassword()), SPECIAL_KEY))) {
                String token = jwtUtil.generateToken(userDetails.getUserId());
                return Mono.just(Tuples.of(user, token));
              }
              return Mono.error(new AuthException(ErrorMessage.LOGIN_FAILED));
            })
        .map(tuple2 -> Tuples.of(AuthMapper.convertToUserDetails(tuple2.getT1()), tuple2.getT2()))
        .defaultIfEmpty(Tuples.of(UserDetails.builder().build(), ""));
  }

  public Mono<UserDetails> getUserDetailsById(String userId) {
    return userRepository
        .findByUserId(userId)
        .map(AuthMapper::convertToUserDetails)
        .defaultIfEmpty(UserDetails.builder().build());
  }

  public Mono<UserDetails> createUser(UserDetails userDetails) {
    userDetails.setPassword(encrypt(userDetails.getPassword(), SPECIAL_KEY));
    userDetails.setSecret(
        Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded()));
    return userRepository
        .save(convertToUser(userDetails))
        .map(AuthMapper::convertToUserDetails)
        .defaultIfEmpty(UserDetails.builder().build());
  }

  public Mono<UserDetails> validateUserId(UserDetails userDetails) {
    log.info("validateUserId :: USER_DETAILS :: {}", userDetails);
    return userRepository
        .countByUserId(userDetails.getUserId())
        .defaultIfEmpty(0)
        .map(n -> n > 0 ? Status.NOT_AVAILABLE.name() : Status.AVAILABLE.name())
        .map(userIdStatus -> UserDetails.builder().userIdStatus(userIdStatus).build());
  }

  public Mono<UserDetails> resetPassword(UserDetails userDetails) {
    log.info("resetPassword :: USER_DETAILS :: {}", userDetails);
    return userRepository
        .findByUserId(userDetails.getUserId())
        .flatMap(
            user -> {
              if (!StringUtils.equalsIgnoreCase(
                  userDetails.getPassword(),
                  decrypt(Base64.getDecoder().decode(user.getPassword()), SPECIAL_KEY))) {
                user.setPassword(encrypt(userDetails.getPassword(), SPECIAL_KEY));
                return Mono.just(user);
              }
              return Mono.error(new AuthException(ErrorMessage.INVALID_PASSWORD));
            })
        .flatMap(userRepository::save)
        .map(AuthMapper::convertToUserDetails);
  }

  public Mono<UserDetails> sendOTP(UserDetails userDetails) {

    return userRepository
        .findByUserId(userDetails.getUserId())
        .map(AuthMapper::convertToUserDetails)
        .map(
            details -> {
              SecretKey key = new SecretKeySpec(details.getSecret().getBytes(), AES_ALGORITHM);
              try {
                details.setOtp(totp.generateOneTimePasswordString(key, Instant.now()));
              } catch (Exception e) {
                System.out.println(e);
              }
              messagingClient.sendEmail(convertToEmailRequest(details)).subscribe();
              return details;
            });
  }

  public Mono<UserDetails> validateOTP(UserDetails userDetails) {

    return userRepository
        .findByUserId(userDetails.getUserId())
        .map(AuthMapper::convertToUserDetails)
        .flatMap(
            details -> {
              SecretKey key = new SecretKeySpec(details.getSecret().getBytes(), AES_ALGORITHM);
              try {
                if (StringUtils.equalsIgnoreCase(
                    totp.generateOneTimePasswordString(key, Instant.now()), userDetails.getOtp())) {
                  return Mono.just(details);
                } else {
                  return Mono.error(new AuthException(ErrorMessage.INVALID_OTP));
                }
              } catch (Exception e) {

                System.out.println(e);
              }
              return Mono.just(details);
            });
  }
}
