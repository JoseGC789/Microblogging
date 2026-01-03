package com.github.josegc789.microblogging.spi.repositories;

import com.github.josegc789.microblogging.spi.entities.UsersDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataMongoUsersRepository extends MongoRepository<UsersDocument, String> {
  boolean existsByUsername(String username);
}
