package com.nasim.moneycopilot.exception;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates exceptions into RFC 7807 Problem Details responses.
 *
 * <p>All error responses share a consistent shape:
 * type, title, status, detail, instance, and a timestamp extension.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Handle validation errors from @Valid — returns field-level messages. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    String detail = ex.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining("; "));

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    problem.setTitle("Validation Failed");
    problem.setType(URI.create("/errors/validation"));
    problem.setProperty("timestamp", OffsetDateTime.now());
    return problem;
  }

  /** Handle resource not found — 404. */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setTitle("Not Found");
    problem.setType(URI.create("/errors/not-found"));
    problem.setProperty("timestamp", OffsetDateTime.now());
    return problem;
  }

  /** Handle duplicate email on registration — 409. */
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ProblemDetail handleEmailConflict(EmailAlreadyExistsException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.CONFLICT, ex.getMessage());
    problem.setTitle("Conflict");
    problem.setType(URI.create("/errors/conflict"));
    problem.setProperty("timestamp", OffsetDateTime.now());
    return problem;
  }

  /** Handle bad login credentials — 401. */
  @ExceptionHandler(BadCredentialsException.class)
  public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED, "Invalid email or password");
    problem.setTitle("Unauthorized");
    problem.setType(URI.create("/errors/unauthorized"));
    problem.setProperty("timestamp", OffsetDateTime.now());
    return problem;
  }

  /** Handle invalid or expired tokens — 401. */
  @ExceptionHandler(InvalidTokenException.class)
  public ProblemDetail handleInvalidToken(InvalidTokenException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.UNAUTHORIZED, ex.getMessage());
    problem.setTitle("Unauthorized");
    problem.setType(URI.create("/errors/unauthorized"));
    problem.setProperty("timestamp", OffsetDateTime.now());
    return problem;
  }

  /** Catch-all for unexpected errors — 500. */
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGeneral(Exception ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    problem.setTitle("Internal Server Error");
    problem.setType(URI.create("/errors/internal"));
    problem.setProperty("timestamp", OffsetDateTime.now());
    return problem;
  }
}
