package com.github.josegc789.microblogging.spi.services;

import com.github.josegc789.microblogging.core.domain.SignInUser;
import com.github.josegc789.microblogging.core.domain.User;
import com.github.josegc789.microblogging.spi.UsersSpi;
import com.github.josegc789.microblogging.spi.entities.UsersDocument;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoUsersRepository;

import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpringDataMongoUserService implements UsersSpi {

  private final SpringDataMongoUsersRepository usersRepository;

  @Override
  @Transactional
  public Optional<String> create(SignInUser signInUser) {
    if (usersRepository.existsByUsername(signInUser.username())) {
      return Optional.empty();
    }

    return Optional.of(
        usersRepository
            .save(
                UsersDocument.builder()
                    .username(signInUser.username())
                    .createdOn(ZonedDateTime.now().toInstant())
                    .build())
            .getId());
  }

  @Override
  public Optional<User> find(String id) {
    return usersRepository.findById(id).map(user -> new User(user.getId(), user.getUsername()));
  }
}
