package com.github.josegc789.microblogging.spi;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "post")
@Value
public class PostDocument {
  @Id @Indexed String id;
  @Indexed String owner;
  String content;
}
