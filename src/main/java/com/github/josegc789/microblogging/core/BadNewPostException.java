package com.github.josegc789.microblogging.core;

import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class BadNewPostException extends RuntimeException {
  private final Errors errors;

  public BadNewPostException(String message, Throwable cause, Errors errors) {
    super(message, cause);
    this.errors = errors;
  }
}
