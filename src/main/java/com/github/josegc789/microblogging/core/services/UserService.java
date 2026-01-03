package com.github.josegc789.microblogging.core.services;

import com.github.josegc789.microblogging.core.Users;
import com.github.josegc789.microblogging.core.domain.BadUserException;
import com.github.josegc789.microblogging.core.domain.SignInUser;
import com.github.josegc789.microblogging.core.domain.User;
import com.github.josegc789.microblogging.spi.UsersSpi;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements Users {
  private final UsersSpi usersSpi;
  private final DomainValidator validator;

  @Override
  public User signIn(SignInUser user) {
    validator.peekUser(user);
    Optional<String> result = usersSpi.create(user);
    String id = result.orElseThrow(() -> new BadUserException("User already exists", null));
    return User.builder().id(id).username(user.username()).build();
  }

  @Override
  public User search(String id) {
    return usersSpi.find(id).orElseThrow(() -> new BadUserException("User doesn't exist", null));
  }
}
