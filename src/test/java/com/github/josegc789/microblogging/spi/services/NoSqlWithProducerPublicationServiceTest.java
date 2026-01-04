package com.github.josegc789.microblogging.spi.services;

import com.github.josegc789.microblogging.core.domain.ExistingPublication;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.core.domain.User;
import com.github.josegc789.microblogging.spi.entities.PublicationDocument;
import com.github.josegc789.microblogging.spi.messaging.KafkaProducerService;
import com.github.josegc789.microblogging.spi.repositories.SpringDataMongoPublicationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoSqlWithProducerPublicationServiceTest {
  @Mock SpringDataMongoPublicationsRepository repository;
  @Mock KafkaProducerService producer;
  @InjectMocks NoSqlWithProducerPublicationService noSqlWithProducerPublicationService;

  @Test
  void testShouldCreate() {
    NewPublication publication =
        NewPublication.builder()
            .authorId(UUID.randomUUID().toString())
            .content(UUID.randomUUID().toString())
            .build();
    User author =
        User.builder()
            .id(UUID.randomUUID().toString())
            .username(UUID.randomUUID().toString())
            .build();
    String expected = UUID.randomUUID().toString();
    when(repository.save(
            argThat(
                pd -> {
                  assertAll(
                      () -> assertEquals(author.id(), pd.getAuthorId()),
                      () -> assertEquals(author.username(), pd.getAuthorUsername()),
                      () -> assertEquals(publication.content(), pd.getContent()),
                      () -> assertNotNull(pd.getCreatedOn()));
                  return true;
                })))
        .thenReturn(PublicationDocument.builder().id(expected).build());
    doNothing().when(producer).send(any());
    String actual = noSqlWithProducerPublicationService.create(publication, author);
    assertEquals(expected, actual);
  }

  @Test
  void testShouldFind() {
    List<String> publications = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    List<PublicationDocument> expected =
        List.of(PublicationDocument.builder().build(), PublicationDocument.builder().build());
    when(repository.findAllById(publications)).thenReturn(expected);
    List<PublicationDocument> actual = noSqlWithProducerPublicationService.find(publications);
    assertEquals(expected, actual);
  }

  @Test
  void testShouldDelete() {
    ExistingPublication expected =
        ExistingPublication.from(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    doNothing().when(repository).deleteByAuthorIdAndId(expected.owner(), expected.id());
    noSqlWithProducerPublicationService.delete(expected);
    verify(repository).deleteByAuthorIdAndId(expected.owner(), expected.id());
  }
}
