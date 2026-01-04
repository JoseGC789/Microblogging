package com.github.josegc789.microblogging.api;

import com.github.josegc789.microblogging.core.Timelines;
import com.github.josegc789.microblogging.core.domain.Timeline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimelinesRestControllerTest {

  @Mock private Timelines timelines;

  @InjectMocks private TimelinesRestController controller;

  @Test
  void testShouldSearchTimeline() {
    List<Timeline> expected = List.of(Timeline.builder().build(), Timeline.builder().build());
    String owner = UUID.randomUUID().toString();
    ZonedDateTime cursor = ZonedDateTime.now();
    when(timelines.search(owner, cursor.toInstant())).thenReturn(expected);
    ResponseEntity<List<Timeline>> actual = controller.getTimeline(owner, cursor);
    assertEquals(expected, actual.getBody());
  }

  @Test
  void testShouldSearchTimelineWithNullInstant() {
    List<Timeline> expected = List.of(Timeline.builder().build(), Timeline.builder().build());
    String owner = UUID.randomUUID().toString();
    when(timelines.search(eq(owner), notNull(Instant.class))).thenReturn(expected);
    ResponseEntity<List<Timeline>> actual = controller.getTimeline(owner, null);
    assertEquals(expected, actual.getBody());
  }
}
