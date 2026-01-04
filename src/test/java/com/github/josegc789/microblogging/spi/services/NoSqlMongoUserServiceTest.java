package com.github.josegc789.microblogging.spi.services;

import com.github.josegc789.microblogging.core.domain.SignInUser;
import com.github.josegc789.microblogging.core.domain.User;
import com.github.josegc789.microblogging.spi.entities.UsersDocument;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoUsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoSqlMongoUserServiceTest {
  @Mock SpringDataMongoUsersRepository usersRepository;
  @InjectMocks NoSqlMongoUserService noSqlMongoUserService;

  @Test
  void testShouldEmptyCreate() {
    SignInUser signInUser = SignInUser.builder().username(UUID.randomUUID().toString()).build();
    when(usersRepository.existsByUsername(signInUser.username())).thenReturn(true);
    Optional<String> actual = noSqlMongoUserService.create(signInUser);
    assertEquals(Optional.empty(), actual);
  }

  @Test
  void testShouldCreate() {
    SignInUser signInUser = SignInUser.builder().username(UUID.randomUUID().toString()).build();
    String expected = UUID.randomUUID().toString();
    when(usersRepository.existsByUsername(signInUser.username())).thenReturn(false);
    when(usersRepository.save(
            argThat(
                ud -> {
                  assertAll(
                      () -> assertEquals(signInUser.username(), ud.getUsername()),
                      () -> assertNotNull(ud.getCreatedOn()));
                  return true;
                })))
        .thenReturn(UsersDocument.builder().id(expected).username(expected).build());
    Optional<String> actual = noSqlMongoUserService.create(signInUser);
    assertEquals(expected, actual.orElseThrow(AssertionError::new));
  }

  @Test
  void testShouldFind() {
    User expected =
        User.builder()
            .id(UUID.randomUUID().toString())
            .username(UUID.randomUUID().toString())
            .build();
    when(usersRepository.findById(expected.id()))
        .thenReturn(
            Optional.of(
                UsersDocument.builder()
                    .id(expected.id())
                    .username(expected.username())
                    .createdOn(Instant.now())
                    .updatedOn(Instant.now())
                    .build()));
    Optional<User> actual = noSqlMongoUserService.find(expected.id());
    assertEquals(expected, actual.orElseThrow(AssertionError::new));
  }
}
