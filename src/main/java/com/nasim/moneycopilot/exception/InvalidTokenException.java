package com.nasim.moneycopilot.exception;

/** Thrown when a JWT is invalid or expired. */
public class InvalidTokenException extends RuntimeException {

  /** Create exception with detail message. */
  public InvalidTokenException(String message) {
    super(message);
  }
}
