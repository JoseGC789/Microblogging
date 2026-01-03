package com.github.josegc789.microblogging.core.services;

import com.github.josegc789.microblogging.core.domain.BadFollowException;
import com.github.josegc789.microblogging.core.domain.BadPublicationException;
import com.github.josegc789.microblogging.core.domain.BadUserException;
import com.github.josegc789.microblogging.core.domain.ExistingPublication;
import com.github.josegc789.microblogging.core.domain.NewFollow;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import java.util.function.Function;

import com.github.josegc789.microblogging.core.domain.SignInUser;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
class DomainValidator {
  private final Validator validator;

  void peekPublication(NewPublication publication) {
    peek(
        publication,
        errors -> new BadPublicationException("Publication is not correct", null, errors));
  }

  void peekPublication(ExistingPublication publication) {
    peek(
        publication,
        errors -> new BadPublicationException("Publication is not correct", null, errors));
  }

  void peekUser(SignInUser user) {
    peek(user, errors -> new BadUserException("User is not correct", null, errors));
  }

  void peekFollow(NewFollow follow) {
    peek(follow, errors -> new BadFollowException("User is not correct", null, errors));
  }

  private <T> void peek(T content, Function<Errors, RuntimeException> ex) {
    Errors errors = validator.validateObject(content);
    errors.failOnError(_ -> ex.apply(errors));
  }
}
