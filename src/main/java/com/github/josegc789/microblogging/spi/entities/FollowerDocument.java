package com.github.josegc789.microblogging.spi.entities;

import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "followers")
@Value
@Builder
public class FollowerDocument {
  @Id @Indexed String follower;
  @Indexed String followee;
}
