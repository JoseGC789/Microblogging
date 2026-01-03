package com.github.josegc789.microblogging.spi.repositories;

import com.github.josegc789.microblogging.spi.entities.FollowerDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataMongoFollowersRepository
    extends MongoRepository<FollowerDocument, String> {
  boolean existsByFollowerAndFollowee(String follower, String followee);

  List<FollowerDocument> findByFollowee(String followee);

  List<FollowerDocument> findByFollower(String follower);

  long countByFollowee(String followee);

  long countByFollower(String follower);
}
