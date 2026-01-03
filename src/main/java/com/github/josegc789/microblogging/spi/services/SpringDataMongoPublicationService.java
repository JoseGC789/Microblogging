package com.github.josegc789.microblogging.spi.services;

import com.github.josegc789.microblogging.core.domain.ExistingPublication;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.spi.PublicationsSpi;
import com.github.josegc789.microblogging.spi.UsersSpi;
import com.github.josegc789.microblogging.spi.entities.PublicationDocument;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoPublicationsRepository;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpringDataMongoPublicationService implements PublicationsSpi {

  private final SpringDataMongoPublicationsRepository repository;

  @Override
  @Transactional
  public String create(NewPublication newPublication) {
    PublicationDocument toSave =
        PublicationDocument.builder()
            .createdBy(newPublication.owner())
            .createdOn(ZonedDateTime.now().toInstant())
            .content(newPublication.content())
            .build();
    PublicationDocument entity = repository.save(toSave);
    return entity.getId();
  }

  @Override
  public void delete(ExistingPublication toDelete) {
    repository.deleteByCreatedByAndId(toDelete.owner(), toDelete.id());
  }
}
