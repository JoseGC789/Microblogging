package com.github.josegc789.microblogging.spi;

import com.github.josegc789.microblogging.core.domain.ExistingPublication;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.spi.entities.PublicationDocument;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoPublicationsRepository;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpringDataMongoPostService implements PostSpi {

  private final SpringDataMongoPublicationsRepository repository;

  @Override
  public String publish(NewPublication newPublication) {
    PublicationDocument toSave =
        PublicationDocument.builder()
            .id(UUID.randomUUID().toString())
            .createdBy(newPublication.owner())
            .createdOn(ZonedDateTime.now().toInstant())
            .content(newPublication.content())
            .build();
    PublicationDocument entity = repository.save(toSave);
    return entity.getId();
  }

  @Override
  public void unpublish(ExistingPublication toDelete) {
    repository.deleteByCreatedByAndId(toDelete.owner(), toDelete.id());
  }
}
