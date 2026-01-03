package com.github.josegc789.microblogging.core.services;

import com.github.josegc789.microblogging.core.Publications;
import com.github.josegc789.microblogging.core.Users;
import com.github.josegc789.microblogging.core.domain.ExistingPublication;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.core.domain.Publication;
import com.github.josegc789.microblogging.core.domain.User;
import com.github.josegc789.microblogging.spi.PublicationsSpi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PublicationService implements Publications {

  private final Users users;
  private final PublicationsSpi publicationsSpi;
  private final DomainValidator validator;

  @Override
  public Publication publish(NewPublication publication) {
    validator.peekPublication(publication);
    User user = users.search(publication.owner());
    String id = publicationsSpi.create(publication);
    return Publication.builder().id(id).owner(user).content(publication.content()).build();
  }

  @Override
  public void unpublish(String owner, String id) {
    ExistingPublication publication = ExistingPublication.from(owner, id);
    validator.peekPublication(publication);
    users.search(publication.owner());
    publicationsSpi.delete(publication);
  }
}
