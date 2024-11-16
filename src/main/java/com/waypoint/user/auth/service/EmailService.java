package com.waypoint.user.auth.service;

import com.waypoint.user.auth.exception.GenericException;
import com.waypoint.user.auth.exception.ErrorMessage;
import com.waypoint.user.auth.utilities.AuthMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.ses.SesClient;

@Service
@Slf4j
public class EmailService {
  private final SesClient sesClient;
  private final String senderEmail;
  private final TemplateEngine templateEngine;

  public EmailService(
      SesClient sesClient,
      @Value("${aws.ses.sender-email}") String senderEmail,
      TemplateEngine templateEngine) {
    this.sesClient = sesClient;
    this.senderEmail = senderEmail;
    this.templateEngine = templateEngine;
  }

  public Mono<String> sendOtpMail(String recipientEmail, String otp, String firstName) {
    log.info(
        "sendOtpMail :: recipientEmail :: {} :: otp :: {} :: firstName :: {}",
        recipientEmail,
        otp,
        firstName);

    return Mono.fromCallable(
            () ->
                sesClient.sendEmail(
                    AuthMapper.getSendEmailRequest(
                        senderEmail, recipientEmail, generateHtmlBody(otp, firstName))))
        .map(
            response -> {
              log.info("OTP Email response, ID: {}", response.messageId());
              return otp; // return OTP if you want to verify on the client side
            })
        .doOnError(
            error -> {
              log.error("Failed to send email: {}", error.getMessage());
              Mono.error(new GenericException(ErrorMessage.FAILED_TO_SEND_OTP));
            });
  }

  private String generateHtmlBody(String otp, String firstName) {
    Context context = new Context();
    context.setVariable("otp", otp);
    context.setVariable("name", firstName);
    return templateEngine.process("otp-email", context);
  }
}
