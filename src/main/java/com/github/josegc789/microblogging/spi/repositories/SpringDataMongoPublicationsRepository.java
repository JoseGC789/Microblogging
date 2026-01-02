package com.github.josegc789.microblogging.spi.repositories;

import com.github.josegc789.microblogging.spi.entities.PublicationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataMongoPublicationsRepository
    extends MongoRepository<PublicationDocument, String> {
  void deleteByCreatedByAndId(String createdBy, String id);
}
