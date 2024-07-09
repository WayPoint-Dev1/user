package com.travelguide.user.auth.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelguide.user.auth.domain.response.ErrorResponse;
import com.travelguide.user.auth.exception.ErrorMessage;
import com.travelguide.user.auth.service.AuthService;
import com.travelguide.user.auth.utilities.JwtUtil;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

  @Autowired private JwtUtil jwtUtil;

  @Autowired private AuthService authService;

  @Autowired private ObjectMapper objectMapper;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String authorizationHeader =
        exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      String token = authorizationHeader.substring(7);
      String username;
      try {
        username = jwtUtil.extractUsername(token);
      } catch (Exception e) {
        return unauthorizedResponse(exchange);
      }

      if (username != null) {
        return authService
            .getUserDetailsById(username)
            .flatMap(
                userDetails -> {
                  if (jwtUtil.validateToken(token, userDetails.getUserId())) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, null);
                    SecurityContext securityContext = new SecurityContextImpl(authenticationToken);
                    return chain
                        .filter(exchange)
                        .contextWrite(
                            ReactiveSecurityContextHolder.withSecurityContext(
                                Mono.just(securityContext)));
                  } else {
                    return unauthorizedResponse(exchange);
                  }
                })
            .onErrorResume(
                e -> {
                  log.error("Authentication error: {}", e.getMessage());
                  return unauthorizedResponse(exchange);
                });
      }
    }
    return chain.filter(exchange);
  }

  private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    String errorResponse = createErrorResponse();
    DataBuffer dataBuffer =
        exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
    return exchange.getResponse().writeWith(Mono.just(dataBuffer));
  }

  private String createErrorResponse() {
    try {
      return objectMapper.writeValueAsString(
          new ErrorResponse(
              ErrorMessage.UNAUTHORIZED.getHttpStatusCode(),
              ErrorMessage.UNAUTHORIZED.getMessageCode()));
    } catch (JsonProcessingException e) {
      log.error("Error creating JSON response", e);
      return "{\"error\": \"Internal server error\"}";
    }
  }
}
