package com.travelguide.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

  private static final Logger logger = LoggerFactory.getLogger(WebFluxConfig.class);

  @Value("${cors.allowed-origins}")
  private String allowedOrigins;

  @Value("${cors.allowed-methods}")
  private String[] allowedMethods;

  @Value("${cors.allowed-headers}")
  private String[] allowedHeaders;

  @Value("${cors.allow-credentials}")
  private boolean allowCredentials;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    logger.info("Configuring CORS with origins: {}", allowedHeaders.length);
    registry
        .addMapping("/**")
        .allowedOrigins(allowedOrigins.split(","))
        .allowedMethods(allowedMethods)
        .allowedHeaders(allowedHeaders)
        .allowCredentials(allowCredentials);
  }
}
