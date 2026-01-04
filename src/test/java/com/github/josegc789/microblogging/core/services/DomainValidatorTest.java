package com.github.josegc789.microblogging.core.services;

import com.github.josegc789.microblogging.core.domain.BadFollowException;
import com.github.josegc789.microblogging.core.domain.BadPublicationException;
import com.github.josegc789.microblogging.core.domain.BadUserException;
import com.github.josegc789.microblogging.core.domain.ExistingPublication;
import com.github.josegc789.microblogging.core.domain.NewFollow;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.core.domain.SignInUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DomainValidatorTest {

  @Mock private Validator validator;
  @Mock private Errors errors;
  private DomainValidator domainValidator;

  @BeforeEach
  void setUp() {
    domainValidator = new DomainValidator(validator);
  }

  @Test
  void peekPublicationWithNoErrorsDoesNotThrow() {
    NewPublication publication =
        new NewPublication(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    when(validator.validateObject(publication)).thenReturn(errors);
    doNothing().when(errors).failOnError(any());
    assertDoesNotThrow(() -> domainValidator.peekPublication(publication));
    verify(validator).validateObject(publication);
    verify(errors).failOnError(any());
  }

  @Test
  void peekPublicationWithErrorsThrowsBadPublicationException() {
    NewPublication publication =
        new NewPublication(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    when(validator.validateObject(publication)).thenReturn(errors);
    doThrow(new BadPublicationException("bad", null, errors)).when(errors).failOnError(any());
    assertThrows(BadPublicationException.class, () -> domainValidator.peekPublication(publication));
  }

  @Test
  void peekExistingPublicationWithErrorsThrowsBadPublicationException() {
    var existing = ExistingPublication.from("owner", "id");
    when(validator.validateObject(existing)).thenReturn(errors);
    doThrow(new BadPublicationException("bad", null, errors)).when(errors).failOnError(any());
    assertThrows(BadPublicationException.class, () -> domainValidator.peekPublication(existing));
  }

  @Test
  void peekUserWithAndWithoutErrors() {
    SignInUser user = new SignInUser("alice");
    when(validator.validateObject(user)).thenReturn(errors);
    doNothing().when(errors).failOnError(any());
    assertDoesNotThrow(() -> domainValidator.peekUser(user));
    doThrow(new BadUserException("bad user", null, errors)).when(errors).failOnError(any());
    assertThrows(BadUserException.class, () -> domainValidator.peekUser(user));
  }

  @Test
  void peekFollowWithAndWithoutErrors() {
    NewFollow follow = new NewFollow("follower-1", "followee-1");
    when(validator.validateObject(follow)).thenReturn(errors);
    doNothing().when(errors).failOnError(any());
    assertDoesNotThrow(() -> domainValidator.peekFollow(follow));
    doThrow(new BadFollowException("bad follow", null, errors)).when(errors).failOnError(any());
    assertThrows(BadFollowException.class, () -> domainValidator.peekFollow(follow));
  }
}
