package com.nasim.moneycopilot.service;

import com.nasim.moneycopilot.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Handles JWT creation and validation.
 *
 * <p>Access tokens identify the user for API requests.
 * Refresh tokens allow issuing new access tokens without re-login.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

  private final JwtProperties jwtProperties;

  /** Generate a short-lived access token for the given user. */
  public String generateAccessToken(UserDetails userDetails) {
    return buildToken(userDetails, jwtProperties.getExpirationMs());
  }

  /** Generate a long-lived refresh token for the given user. */
  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(userDetails, jwtProperties.getRefreshExpirationMs());
  }

  /** Extract the username (email) from a token. */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /** Return true if the token is valid and belongs to the given user. */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private String buildToken(UserDetails userDetails, long expirationMs) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .subject(userDetails.getUsername())
        .issuedAt(new Date(now))
        .expiration(new Date(now + expirationMs))
        .signWith(signingKey())
        .compact();
  }

  private boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    Claims claims = Jwts.parser()
        .verifyWith(signingKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
    return claimsResolver.apply(claims);
  }

  private SecretKey signingKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }
}
