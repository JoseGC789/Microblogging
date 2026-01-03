package com.github.josegc789.microblogging.spi.repositories;

import com.github.josegc789.microblogging.spi.entities.PublicationDocument;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataMongoPublicationsRepository
    extends MongoRepository<PublicationDocument, String> {
  void deleteByAuthorIdAndId(String authorId, String id);

  List<PublicationDocument> findByAuthorIdInOrderByCreatedOnDesc(
      List<String> authorId, Pageable pageable);

  List<PublicationDocument> findByAuthorIdInAndCreatedOnLessThanOrderByCreatedOnDesc(
      List<String> authorId, Instant createdOn, Pageable pageable);
}
