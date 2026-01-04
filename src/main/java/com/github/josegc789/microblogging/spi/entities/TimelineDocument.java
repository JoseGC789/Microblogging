package com.github.josegc789.microblogging.spi.entities;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "timelines")
@Value
@Builder
@CompoundIndexes({
  @CompoundIndex(name = "by_owner_date", def = "{'owner': 1, 'createdOn': -1}"),
  @CompoundIndex(name = "by_authorId_date", def = "{'authorId': 1, 'createdOn': -1}"),
  @CompoundIndex(
      name = "by_authorId_publicationId_date",
      def = "{'authorId': 1, 'publicationId': 1, 'createdOn': -1}")
})
public class TimelineDocument {
  @Id String id;
  String owner;
  String publicationId;
  String authorId;
  String authorUsername;
  String content;
  Instant createdOn;
}
