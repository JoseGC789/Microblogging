package com.github.josegc789.microblogging.api;

import com.github.josegc789.microblogging.core.Followers;
import com.github.josegc789.microblogging.core.Users;
import com.github.josegc789.microblogging.core.domain.Follow;
import com.github.josegc789.microblogging.core.domain.NewFollow;
import com.github.josegc789.microblogging.core.domain.SignInUser;
import com.github.josegc789.microblogging.core.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersRestControllerTest {

  @Mock private Users users;
  @Mock private Followers followers;
  @InjectMocks private UsersRestController controller;

  @Test
  void testShouldSignIn() {
    SignInUser signInUser = SignInUser.builder().username(UUID.randomUUID().toString()).build();
    User expected =
        User.builder().id(UUID.randomUUID().toString()).username(signInUser.username()).build();
    when(users.signIn(signInUser)).thenReturn(expected);
    ResponseEntity<User> actual = controller.signIn(signInUser);
    assertEquals(expected, actual.getBody());
  }

  @Test
  void testShouldFollow() {
    NewFollow newFollow =
        NewFollow.builder()
            .follower(UUID.randomUUID().toString())
            .followee(UUID.randomUUID().toString())
            .build();
    Follow expected =
        Follow.builder()
            .follower(
                User.builder()
                    .id(newFollow.follower())
                    .username(UUID.randomUUID().toString())
                    .build())
            .followee(
                User.builder()
                    .id(newFollow.followee())
                    .username(UUID.randomUUID().toString())
                    .build())
            .id(UUID.randomUUID().toString())
            .build();
    when(followers.follow(newFollow)).thenReturn(expected);
    ResponseEntity<Follow> actual = controller.follow(newFollow);
    assertEquals(expected, actual.getBody());
  }
}
