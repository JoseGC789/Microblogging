package com.github.josegc789.microblogging.spi.services;

import com.github.josegc789.microblogging.core.domain.ExistingPublication;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.core.domain.User;
import com.github.josegc789.microblogging.spi.PublicationsSpi;
import com.github.josegc789.microblogging.spi.entities.PublicationDocument;
import com.github.josegc789.microblogging.spi.entities.PublicationEvent;
import com.github.josegc789.microblogging.spi.messaging.KafkaProducerService;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoPublicationsRepository;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoSqlWithProducerPublicationService implements PublicationsSpi {

  private final SpringDataMongoPublicationsRepository repository;
  private final KafkaProducerService producer;

  @Override
  @Transactional
  public String create(NewPublication newPublication, User author) {
    PublicationDocument toSave =
        PublicationDocument.builder()
            .authorUsername(author.username())
            .authorId(author.id())
            .createdOn(ZonedDateTime.now().toInstant())
            .content(newPublication.content())
            .build();
    PublicationDocument saved = repository.save(toSave);
    producer.send(
        new PublicationEvent(
            saved.getId(),
            saved.getAuthorUsername(),
            saved.getAuthorId(),
            saved.getContent(),
            saved.getCreatedOn()));
    return saved.getId();
  }

  @Override
  public List<PublicationDocument> find(List<String> publications) {
    return repository.findAllById(publications);
  }

  @Override
  public void delete(ExistingPublication toDelete) {
    repository.deleteByAuthorIdAndId(toDelete.owner(), toDelete.id());
  }
}
