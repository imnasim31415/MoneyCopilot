package com.nasim.moneycopilot.exception;

/** Thrown when a registration attempt uses an email that is already taken. */
public class EmailAlreadyExistsException extends RuntimeException {

  /** Create exception with the conflicting email. */
  public EmailAlreadyExistsException(String email) {
    super("Email already registered: " + email);
  }
}
