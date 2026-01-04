package com.github.josegc789.microblogging.api;

import com.github.josegc789.microblogging.core.domain.BadPublicationException;
import com.github.josegc789.microblogging.core.domain.BadUserException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestAdvisorTest {

  private final RestAdvisor advisor = new RestAdvisor();

  @Test
  void testShouldProblemDetailOnException() {
    String expected = UUID.randomUUID().toString();
    ResponseEntity<ProblemDetail> actual = advisor.handleException(new Exception(expected));
    assertEquals(expected, actual.getBody().getTitle(), expected);
    assertTrue(actual.getBody().getDetail().contains(expected));
  }

  @Test
  void testShouldProblemDetailOnRuntimeException() {
    String expected = UUID.randomUUID().toString();
    ResponseEntity<ProblemDetail> actual =
        advisor.handleRuntimeException(new RuntimeException(expected));
    assertEquals(expected, actual.getBody().getTitle(), expected);
    assertTrue(actual.getBody().getDetail().contains(expected));
  }

  @Test
  void testShouldHandleOnDomainBadPublication() {
    BadPublicationException ex = new BadPublicationException("bad pub", null, null);

    ResponseEntity<ProblemDetail> resp = advisor.handleBadRequest(ex);
    assertThat(resp).isNotNull();
    ProblemDetail detail = resp.getBody();
    assertThat(detail).isNotNull();
    assertThat(detail.getTitle()).isEqualTo("bad pub");
    Object errorsProp = detail.getProperties().get("errors");
    assertThat(errorsProp).isNotNull();
  }

  @Test
  void testShouldHandleOnDomainBadUser() {
    BadUserException ex = new BadUserException("bad user", null, null);
    ResponseEntity<ProblemDetail> resp = advisor.handleBadRequest(ex);
    assertThat(resp).isNotNull();
    ProblemDetail detail = resp.getBody();
    assertThat(detail).isNotNull();
    assertThat(detail.getTitle()).isEqualTo("bad user");
    Object errorsProp = detail.getProperties().get("errors");
    assertThat(errorsProp).isNotNull();
  }
}
