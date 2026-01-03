package com.github.josegc789.microblogging.spi.repositories;

import com.github.josegc789.microblogging.spi.entities.TimelineDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface SpringDataMongoTimelinesRepository
    extends MongoRepository<TimelineDocument, String> {
  List<TimelineDocument> findByOwnerOrderByCreatedOnDesc(String owner, Pageable pageable);

  List<TimelineDocument> findByOwnerAndCreatedOnLessThanOrderByCreatedOnDesc(
      String owner, Instant createdOn, Pageable pageable);
}
