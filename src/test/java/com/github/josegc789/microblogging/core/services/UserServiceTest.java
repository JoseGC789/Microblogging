package com.github.josegc789.microblogging.core.services;

import com.github.josegc789.microblogging.core.domain.BadUserException;
import com.github.josegc789.microblogging.core.domain.SignInUser;
import com.github.josegc789.microblogging.core.domain.User;
import com.github.josegc789.microblogging.spi.UsersSpi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UsersSpi usersSpi;
  @Mock private DomainValidator validator;

  @InjectMocks private UserService userService;

  @Test
  void testShouldSignIn() {
    User expected =
        User.builder()
            .id(UUID.randomUUID().toString())
            .username(UUID.randomUUID().toString())
            .build();
    SignInUser signInUser = SignInUser.builder().username(expected.username()).build();
    doNothing().when(validator).peekUser(signInUser);
    when(usersSpi.create(signInUser)).thenReturn(Optional.of(expected.id()));
    User actual = userService.signIn(signInUser);
    assertEquals(expected, actual);
  }

  @Test
  void testShouldSignInFailing() {
    doNothing().when(validator).peekUser(any());
    when(usersSpi.create(any())).thenReturn(Optional.empty());
    assertThrows(BadUserException.class, () -> userService.signIn(SignInUser.builder().build()));
  }

  @Test
  void testShouldSearch() {
    User expected =
        User.builder()
            .id(UUID.randomUUID().toString())
            .username(UUID.randomUUID().toString())
            .build();
    when(usersSpi.find(expected.id())).thenReturn(Optional.of(expected));
    User actual = userService.search(expected.id());
    assertEquals(expected, actual);
  }

  @Test
  void testShouldSearchFailing() {
    when(usersSpi.find(any())).thenReturn(Optional.empty());
    assertThrows(BadUserException.class, () -> userService.search(UUID.randomUUID().toString()));
  }
}
