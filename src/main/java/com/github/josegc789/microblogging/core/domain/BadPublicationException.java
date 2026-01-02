package com.github.josegc789.microblogging.core.domain;

import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class BadPublicationException extends RuntimeException {
  private final Errors errors;

  public BadPublicationException(String message, Throwable cause, Errors errors) {
    super(message, cause);
    this.errors = errors;
  }
}
