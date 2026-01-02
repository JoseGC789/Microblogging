package com.github.josegc789.microblogging.spi;

import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataMongoPostRepository extends MongoRepository<PostDocument, UUID> {}
