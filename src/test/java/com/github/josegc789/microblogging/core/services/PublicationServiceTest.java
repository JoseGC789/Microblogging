package com.github.josegc789.microblogging.core.services;

import com.github.josegc789.microblogging.core.Users;
import com.github.josegc789.microblogging.core.domain.ExistingPublication;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.core.domain.Publication;
import com.github.josegc789.microblogging.core.domain.User;
import com.github.josegc789.microblogging.spi.PublicationsSpi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicationServiceTest {

  @Mock private Users users;
  @Mock private PublicationsSpi publicationsSpi;
  @Mock private DomainValidator validator;

  @InjectMocks private PublicationService publicationService;

  @Test
  void testShouldPublish() {
    Publication expected =
        Publication.builder()
            .id(UUID.randomUUID().toString())
            .content(UUID.randomUUID().toString())
            .author(
                User.builder()
                    .id(UUID.randomUUID().toString())
                    .username(UUID.randomUUID().toString())
                    .build())
            .build();
    NewPublication newPublication =
        NewPublication.builder()
            .content(expected.content())
            .authorId(expected.author().id())
            .build();
    doNothing().when(validator).peekPublication(newPublication);
    when(users.search(expected.author().id())).thenReturn(expected.author());
    when(publicationsSpi.create(newPublication, expected.author())).thenReturn(expected.id());
    Publication actual = publicationService.publish(newPublication);
    assertEquals(expected, actual);
  }

  @Test
  void testShouldUnpublish() {
    String owner = UUID.randomUUID().toString();
    String id = UUID.randomUUID().toString();
    ExistingPublication expected = ExistingPublication.from(owner, id);
    doNothing().when(validator).peekPublication(expected);
    doNothing().when(publicationsSpi).delete(expected);
    when(users.search(expected.owner())).thenReturn(User.builder().build());
    publicationService.unpublish(owner, id);
  }
}
