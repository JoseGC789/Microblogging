package com.github.josegc789.microblogging.spi.repositories;

import com.github.josegc789.microblogging.spi.entities.FollowerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataMongoFollowersRepository
    extends MongoRepository<FollowerDocument, String> {}
