package com.nasim.moneycopilot.exception;

/** Thrown when a requested resource does not exist or belongs to another user. */
public class ResourceNotFoundException extends RuntimeException {

  /** Create exception for a resource type and id. */
  public ResourceNotFoundException(String resourceType, Object id) {
    super(resourceType + " not found: " + id);
  }
}
