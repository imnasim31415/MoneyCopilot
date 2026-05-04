package com.nasim.moneycopilot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nasim.moneycopilot.exception.EmailAlreadyExistsException;
import com.nasim.moneycopilot.model.dto.AuthResponse;
import com.nasim.moneycopilot.model.dto.RegisterRequest;
import com.nasim.moneycopilot.model.entity.User;
import com.nasim.moneycopilot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtService jwtService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private UserDetailsService userDetailsService;

  @InjectMocks
  private AuthService authService;

  @Test
  void should_returnTokens_when_registrationIsSuccessful() {
    RegisterRequest request = new RegisterRequest("test@example.com", "Password1");

    when(userRepository.existsByEmail(request.email())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("hashed");
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
    when(jwtService.generateAccessToken(any())).thenReturn("access-token");
    when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

    AuthResponse response = authService.register(request);

    assertThat(response.accessToken()).isEqualTo("access-token");
    assertThat(response.refreshToken()).isEqualTo("refresh-token");
  }

  @Test
  void should_throwException_when_emailAlreadyExists() {
    RegisterRequest request = new RegisterRequest("existing@example.com", "Password1");

    when(userRepository.existsByEmail(request.email())).thenReturn(true);

    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(EmailAlreadyExistsException.class);

    verify(userRepository, never()).save(any());
  }
}
