package com.example.loyalty.common.exceptions;

/** Exception thrown when a requested resource is not found */
public class NotFoundException extends LoyaltyException {
  private final String resource;
  private final String resourceId;

  public NotFoundException(String resource, String resourceId) {
    super(String.format("%s with ID %s not found", resource, resourceId), "NOT_FOUND", 404);
    this.resource = resource;
    this.resourceId = resourceId;
  }

  public NotFoundException(String message) {
    super(message, "NOT_FOUND", 404);
    this.resource = null;
    this.resourceId = null;
  }

  public String getResource() {
    return resource;
  }

  public String getResourceId() {
    return resourceId;
  }
}
