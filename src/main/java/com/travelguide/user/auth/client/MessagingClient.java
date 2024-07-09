package com.travelguide.user.auth.client;

import static com.travelguide.user.auth.constants.AuthConstants.NOTIFY_USER_URI;

import com.travelguide.user.auth.domain.request.EmailRequest;
import com.travelguide.user.auth.domain.response.EmailResponse;
import java.net.ConnectException;
import java.time.Duration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class MessagingClient {
  public WebClient.Builder webClientBuilder;
  public String baseEndPoint;

  public MessagingClient(WebClient.Builder webClientBuilder, final String baseEndpoint) {
    this.webClientBuilder = webClientBuilder;
    this.baseEndPoint = baseEndpoint;
  }

  public Mono<EmailResponse> sendEmail(EmailRequest emailRequest) {
    return Mono.defer(
            () ->
                webClientBuilder
                    .build()
                    .post()
                    .uri(baseEndPoint.concat(NOTIFY_USER_URI))
                    .bodyValue(emailRequest)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(EmailResponse.class))
        .retryWhen(
            Retry.backoff(3, Duration.ofSeconds(10))
                .filter(throwable -> throwable.getCause().getCause() instanceof ConnectException));
  }
}
