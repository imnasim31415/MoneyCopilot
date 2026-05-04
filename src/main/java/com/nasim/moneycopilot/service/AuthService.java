package com.nasim.moneycopilot.service;

import com.nasim.moneycopilot.exception.EmailAlreadyExistsException;
import com.nasim.moneycopilot.exception.InvalidTokenException;
import com.nasim.moneycopilot.model.dto.AuthResponse;
import com.nasim.moneycopilot.model.dto.LoginRequest;
import com.nasim.moneycopilot.model.dto.RefreshRequest;
import com.nasim.moneycopilot.model.dto.RegisterRequest;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Handles user registration, login, and token refresh. */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;

  /**
   * Register a new user and return tokens.
   *
   * @throws EmailAlreadyExistsException if the email is already taken
   */
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new EmailAlreadyExistsException(request.email());
    }

    User user = User.builder()
        .email(request.email())
        .passwordHash(passwordEncoder.encode(request.password()))
        .build();

    userRepository.save(user);

    return new AuthResponse(
        jwtService.generateAccessToken(user),
        jwtService.generateRefreshToken(user)
    );
  }

  /** Authenticate a user and return tokens. */
  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password())
    );

    User user = (User) userDetailsService.loadUserByUsername(request.email());

    return new AuthResponse(
        jwtService.generateAccessToken(user),
        jwtService.generateRefreshToken(user)
    );
  }

  /**
   * Issue a new access token from a valid refresh token.
   *
   * @throws InvalidTokenException if the refresh token is invalid or expired
   */
  public AuthResponse refresh(RefreshRequest request) {
    String email;
    try {
      email = jwtService.extractUsername(request.refreshToken());
    } catch (Exception e) {
      throw new InvalidTokenException("Invalid refresh token");
    }

    User user = (User) userDetailsService.loadUserByUsername(email);

    if (!jwtService.isTokenValid(request.refreshToken(), user)) {
      throw new InvalidTokenException("Refresh token expired or invalid");
    }

    return new AuthResponse(
        jwtService.generateAccessToken(user),
        request.refreshToken()
    );
  }
}
