package com.travelguide.user.config;

import com.travelguide.user.auth.client.MessagingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
  @Value("${ems.msg.base-endpoint}")
  private String msgEndPoint;

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  @Bean
  public MessagingClient getMessagingClient() {
    return new MessagingClient(webClientBuilder(), msgEndPoint);
  }
}
