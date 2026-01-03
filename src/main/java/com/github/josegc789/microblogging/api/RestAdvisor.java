package com.github.josegc789.microblogging.api;

import com.github.josegc789.microblogging.core.domain.BadPublicationException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import com.github.josegc789.microblogging.core.domain.BadUserException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class RestAdvisor {
  private static final String DETAIL_BEGINNING = "The request's ";
  private static final Supplier<String> GENERIC = () -> UUID.randomUUID().toString();

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleException(Exception ex) {
    log.error("Internal Error {}", ex.getMessage());
    ex.printStackTrace();
    return ResponseEntity.internalServerError()
        .body(toDetail(ex, HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException ex) {
    ex.printStackTrace();
    log.error("Internal Error {}", ex.getMessage());
    return ResponseEntity.internalServerError()
        .body(toDetail(ex, HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @ExceptionHandler(BadPublicationException.class)
  public ResponseEntity<ProblemDetail> handleBadRequest(BadPublicationException ex) {
    return ResponseEntity.badRequest().body(toDetail(ex, ex.getErrors(), HttpStatus.BAD_REQUEST));
  }

  @ExceptionHandler(BadUserException.class)
  public ResponseEntity<ProblemDetail> handleBadRequest(BadUserException ex) {
    return ResponseEntity.badRequest().body(toDetail(ex, ex.getErrors(), HttpStatus.BAD_REQUEST));
  }

  private ProblemDetail toDetail(Exception ex, Errors errors, HttpStatus status) {
    ProblemDetail detail = toDetail(ex, status);
    detail.setProperties(
        Map.of(
            "errors",
            Optional.ofNullable(errors).map(Errors::getAllErrors).orElse(Collections.emptyList())));
    return detail;
  }

  private ProblemDetail toDetail(Exception ex, HttpStatus status) {
    ProblemDetail detail = ProblemDetail.forStatus(status);
    String message = Optional.ofNullable(ex).map(Exception::getMessage).orElse(GENERIC.get());
    detail.setTitle(message);
    detail.setDetail(DETAIL_BEGINNING + message);
    detail.setType(URI.create("https://unimplemented.test"));
    detail.setInstance(URI.create("https://unimplemented.test"));
    return detail;
  }
}
