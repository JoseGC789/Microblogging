package com.github.josegc789.microblogging.spi.entities;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "followers")
@Value
@Builder
public class FollowerDocument {
  @Id String id;
  @Indexed String follower;
  @Indexed String followee;
  @CreatedDate Instant createdOn;
  @LastModifiedDate Instant updatedOn;
}
