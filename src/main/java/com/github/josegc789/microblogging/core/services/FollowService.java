package com.github.josegc789.microblogging.core.services;

import com.github.josegc789.microblogging.core.Followers;
import com.github.josegc789.microblogging.core.Users;
import com.github.josegc789.microblogging.core.domain.BadUserException;
import com.github.josegc789.microblogging.core.domain.Follow;
import com.github.josegc789.microblogging.core.domain.NewFollow;
import com.github.josegc789.microblogging.spi.FollowersSpi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FollowService implements Followers {
  private final Users users;
  private final FollowersSpi followerSpi;
  private final DomainValidator validator;

  @Override
  public Follow follow(NewFollow newFollower) {
    validator.peekFollow(newFollower);
    return Follow.builder()
        .follower(users.search(newFollower.follower()))
        .followee(users.search(newFollower.followee()))
        .id(
            followerSpi
                .follow(newFollower)
                .orElseThrow(() -> new BadUserException("Follow already exists", null)))
        .build();
  }
}
