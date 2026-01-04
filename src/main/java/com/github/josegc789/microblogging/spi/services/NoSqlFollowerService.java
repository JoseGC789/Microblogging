package com.github.josegc789.microblogging.spi.services;

import com.github.josegc789.microblogging.core.domain.NewFollow;
import com.github.josegc789.microblogging.spi.FollowersSpi;
import com.github.josegc789.microblogging.spi.entities.FollowerDocument;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoFollowersRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoSqlFollowerService implements FollowersSpi {

  private final SpringDataMongoFollowersRepository followersRepository;

  @Override
  @Transactional
  public Optional<String> follow(NewFollow follow) {
    if (followersRepository.existsByFollowerAndFollowee(follow.follower(), follow.followee())) {
      return Optional.empty();
    }

    return Optional.of(
        followersRepository
            .save(
                FollowerDocument.builder()
                    .follower(follow.follower())
                    .followee(follow.followee())
                    .createdOn(ZonedDateTime.now().toInstant())
                    .build())
            .getId());
  }

  @Override
  public List<String> followersOf(String followee) {
    return followersRepository.findByFollowee(followee).stream()
        .map(FollowerDocument::getFollower)
        .toList();
  }

  @Override
  public List<String> followeesOf(String follower) {
    return followersRepository.findByFollower(follower).stream()
        .map(FollowerDocument::getFollowee)
        .toList();
  }
}
