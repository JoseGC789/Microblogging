package com.github.josegc789.microblogging.core;

import com.github.josegc789.microblogging.spi.PostSpi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Service
public class PostService implements MicroBlogging {

  private final Validator validator;
  private final PostSpi postSpi;

  @Override
  public NewPost post(NewPost post) {
    validateFailing(post);
    return post.withId(postSpi.publish(post));
  }

  private void validateFailing(NewPost content) {
    Errors errors = validator.validateObject(content);
    errors.failOnError(_ -> new BadNewPostException("New post is not correct", null, errors));
  }
}
