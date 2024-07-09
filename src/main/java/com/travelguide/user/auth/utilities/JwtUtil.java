package com.travelguide.user.auth.utilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

  public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
        .signWith(secretKey)
        .compact();
  }

  public Claims verifyToken(String token) {
    return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
  }

  public Boolean validateToken(String token, String username) {
    Claims claims = verifyToken(token);
    final String extractedUsername = claims.getSubject();
    return (extractedUsername.equals(username) && !isTokenExpired(claims));
  }

  public String extractUsername(String token) {
    return verifyToken(token).getSubject();
  }

  public Date extractExpiration(String token) {
    return verifyToken(token).getExpiration();
  }

  private Boolean isTokenExpired(Claims claims) {
    return claims.getExpiration().before(new Date());
  }
}
