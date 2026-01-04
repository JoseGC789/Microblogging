package com.github.josegc789.microblogging.spi.repositories;

import com.github.josegc789.microblogging.spi.entities.TimelineDocument;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataMongoTimelinesRepository
    extends MongoRepository<TimelineDocument, String> {
  List<TimelineDocument> findByAuthorIdInAndPublicationIdNotIn(
      List<String> authorIds, List<String> publicationIds, Pageable pageable);

  List<TimelineDocument>
      findByAuthorIdInAndPublicationIdNotInAndCreatedOnLessThanOrderByCreatedOnDesc(
          List<String> authorIds,
          List<String> publicationIds,
          Instant createdOn,
          Pageable pageable);

  List<TimelineDocument> findByOwnerOrderByCreatedOnDesc(String owner, Pageable pageable);

  List<TimelineDocument> findByOwnerAndCreatedOnLessThanOrderByCreatedOnDesc(
      String owner, Instant createdOn, Pageable pageable);
}
