package com.github.josegc789.microblogging.core;

import com.github.josegc789.microblogging.core.domain.BadPublicationException;
import com.github.josegc789.microblogging.core.domain.ExistingPublication;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.core.domain.Publication;
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
  public Publication publish(NewPublication publication) {
    validateFailing(publication);
    String id = postSpi.publish(publication);
    return Publication.builder()
        .id(id)
        .owner(publication.owner())
        .content(publication.content())
        .build();
  }

  @Override
  public void unpublish(String owner, String id) {
    ExistingPublication publication = ExistingPublication.from(owner, id);
    validateFailing(publication);
    postSpi.unpublish(publication);
  }

  private <T> void validateFailing(T content) {
    Errors errors = validator.validateObject(content);
    errors.failOnError(
        _ -> new BadPublicationException("Publication is not correct", null, errors));
  }
}
