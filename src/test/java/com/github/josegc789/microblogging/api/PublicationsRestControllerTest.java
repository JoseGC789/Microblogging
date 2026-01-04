package com.github.josegc789.microblogging.api;

import com.github.josegc789.microblogging.core.Publications;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.core.domain.Publication;
import com.github.josegc789.microblogging.core.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublicationsRestControllerTest {

  @Mock private Publications publications;
  @InjectMocks private PublicationsRestController controller;

  @Test
  void testShouldPublish() {
    NewPublication rq =
        new NewPublication(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    Publication expected =
        new Publication(
            UUID.randomUUID().toString(),
            new User(rq.authorId(), UUID.randomUUID().toString()),
            rq.content());
    when(publications.publish(rq)).thenReturn(expected);
    ResponseEntity<Publication> actual = controller.publish(rq);
    assertEquals(expected, actual.getBody());
  }

  @Test
  void testShouldUnpublish() {
    String owner = UUID.randomUUID().toString();
    String id = UUID.randomUUID().toString();
    ResponseEntity<Void> response = controller.delete(owner, id);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(publications, times(1)).unpublish(owner, id);
  }
}
