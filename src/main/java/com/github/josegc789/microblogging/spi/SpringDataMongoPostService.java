package com.github.josegc789.microblogging.spi;

import com.github.josegc789.microblogging.core.NewPost;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpringDataMongoPostService implements PostSpi {

  private final SpringDataMongoPostRepository repository;

  @Override
  public String publish(NewPost newPost) {
    PostDocument toSave =
        new PostDocument(UUID.randomUUID().toString(), newPost.owner(), newPost.content());
    PostDocument entity = repository.save(toSave);
    return entity.getId();
  }
}
