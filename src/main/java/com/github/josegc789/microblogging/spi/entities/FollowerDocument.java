package com.github.josegc789.microblogging.spi.entities;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "followers")
@Value
@Builder
@CompoundIndexes({
  @CompoundIndex(name = "unique_follower_followee", def = "{'follower': 1, 'followee': 1}", unique = true),
  @CompoundIndex(name = "unique_followee", def = "{'followee': 1}")
})
public class FollowerDocument {
  @Id String id;
  String follower;
  String followee;
  @CreatedDate Instant createdOn;
  @LastModifiedDate Instant updatedOn;
}
