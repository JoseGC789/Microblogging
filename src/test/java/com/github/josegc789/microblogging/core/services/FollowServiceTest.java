package com.github.josegc789.microblogging.core.services;

import com.github.josegc789.microblogging.core.Users;
import com.github.josegc789.microblogging.core.domain.BadFollowException;
import com.github.josegc789.microblogging.core.domain.Follow;
import com.github.josegc789.microblogging.core.domain.NewFollow;
import com.github.josegc789.microblogging.core.domain.User;
import com.github.josegc789.microblogging.spi.FollowersSpi;
import com.github.josegc789.microblogging.spi.TimelinesSpi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

  @Mock private Users users;
  @Mock private FollowersSpi followersSpi;
  @Mock private TimelinesSpi timelinesSpi;
  @Mock private DomainValidator validator;

  @InjectMocks private FollowService followService;

  @Test
  void testShouldFollow() {
    NewFollow newFollow =
        NewFollow.builder()
            .follower(UUID.randomUUID().toString())
            .followee(UUID.randomUUID().toString())
            .build();
    Follow expected =
        Follow.builder()
            .id(UUID.randomUUID().toString())
            .follower(
                User.builder()
                    .username(newFollow.follower())
                    .id(UUID.randomUUID().toString())
                    .build())
            .followee(
                User.builder()
                    .username(newFollow.followee())
                    .id(UUID.randomUUID().toString())
                    .build())
            .build();
    when(users.search(newFollow.followee())).thenReturn(expected.followee());
    when(users.search(newFollow.follower())).thenReturn(expected.follower());
    when(followersSpi.follow(newFollow)).thenReturn(Optional.of(expected.id()));
    doNothing().when(timelinesSpi).materialize(expected);
    doNothing().when(validator).peekFollow(newFollow);
    Follow actual = followService.follow(newFollow);
    assertEquals(expected, actual);
  }

  @Test
  void testShouldThrowOnFailSpi() {
    when(users.search(any())).thenReturn(User.builder().build());
    when(users.search(any())).thenReturn(User.builder().build());
    when(followersSpi.follow(any())).thenReturn(Optional.empty());
    doNothing().when(validator).peekFollow(any());
    assertThrows(
        BadFollowException.class,
        () ->
            followService.follow(
                NewFollow.builder()
                    .follower(UUID.randomUUID().toString())
                    .followee(UUID.randomUUID().toString())
                    .build()));
  }
}
