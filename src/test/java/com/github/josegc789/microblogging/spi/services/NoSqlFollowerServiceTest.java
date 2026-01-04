package com.github.josegc789.microblogging.spi.services;

import com.github.josegc789.microblogging.core.domain.NewFollow;
import com.github.josegc789.microblogging.spi.entities.FollowerDocument;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoFollowersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoSqlFollowerServiceTest {
  @Mock SpringDataMongoFollowersRepository followersRepository;
  @InjectMocks NoSqlFollowerService noSqlFollowerService;

  @Test
  void testShouldEmptyFollowIfNotExists() {
    NewFollow newFollow =
        NewFollow.builder()
            .follower(UUID.randomUUID().toString())
            .followee(UUID.randomUUID().toString())
            .build();
    when(followersRepository.existsByFollowerAndFollowee(
            newFollow.follower(), newFollow.followee()))
        .thenReturn(true);
    Optional<String> actual = noSqlFollowerService.follow(newFollow);
    assertEquals(Optional.empty(), actual);
  }

  @Test
  void testShouldFollow() {
    NewFollow newFollow =
        NewFollow.builder()
            .follower(UUID.randomUUID().toString())
            .followee(UUID.randomUUID().toString())
            .build();
    when(followersRepository.existsByFollowerAndFollowee(
            newFollow.follower(), newFollow.followee()))
        .thenReturn(false);
    String expected = UUID.randomUUID().toString();
    when(followersRepository.save(
            argThat(
                document -> {
                  assertAll(
                      () -> assertEquals(newFollow.follower(), document.getFollower()),
                      () -> assertEquals(newFollow.followee(), document.getFollowee()),
                      () -> assertNotNull(document.getCreatedOn()));
                  return true;
                })))
        .thenReturn(FollowerDocument.builder().id(expected).build());
    Optional<String> actual = noSqlFollowerService.follow(newFollow);
    assertEquals(expected, actual.orElseThrow(AssertionError::new));
  }

  @Test
  void testShouldSearchFollowersOf() {
    String expected = UUID.randomUUID().toString();
    when(followersRepository.findByFollowee(expected))
        .thenReturn(
            List.of(FollowerDocument.builder().follower(expected).followee(expected).build()));
    List<String> actual = noSqlFollowerService.followersOf(expected);
    assertEquals(List.of(expected), actual);
  }

  @Test
  void testShouldSearchFolloweesOf() {
    String expected = UUID.randomUUID().toString();
    when(followersRepository.findByFollower(expected))
        .thenReturn(
            List.of(FollowerDocument.builder().follower(expected).followee(expected).build()));
    List<String> actual = noSqlFollowerService.followeesOf(expected);
    assertEquals(List.of(expected), actual);
  }
}
