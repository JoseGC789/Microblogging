package com.github.josegc789.microblogging.spi.entities;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
@Value
@Builder
public class UsersDocument {
  @Id String id;

  @Indexed(unique = true)
  String username;

  @CreatedBy @CreatedDate Instant createdOn;
  @LastModifiedDate Instant updatedOn;
}
