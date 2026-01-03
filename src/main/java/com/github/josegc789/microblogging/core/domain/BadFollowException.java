package com.github.josegc789.microblogging.core.domain;

import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class BadFollowException extends RuntimeException {
  private final Errors errors;

  public BadFollowException(String message, Throwable cause) {
    super(message, cause);
    this.errors = null;
  }

  public BadFollowException(String message, Throwable cause, Errors errors) {
    super(message, cause);
    this.errors = errors;
  }
}
