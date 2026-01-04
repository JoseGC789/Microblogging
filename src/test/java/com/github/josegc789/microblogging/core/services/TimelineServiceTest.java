package com.github.josegc789.microblogging.core.services;

import com.github.josegc789.microblogging.core.domain.Timeline;
import com.github.josegc789.microblogging.spi.TimelinesSpi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimelineServiceTest {

  @Mock private TimelinesSpi timelinesSpi;

  @InjectMocks private TimelineService timelineService;

  @Test
  void testShouldSearchWithCursor() {
    List<Timeline> expected = List.of(Timeline.builder().build(), Timeline.builder().build());
    String owner = UUID.randomUUID().toString();
    Instant cursor = Instant.now();
    when(timelinesSpi.find(owner, 100, cursor)).thenReturn(expected);
    List<Timeline> actual = timelineService.search(owner, cursor);
    assertEquals(expected, actual);
  }

  @Test
  void testShouldSearch() {
    List<Timeline> expected = List.of(Timeline.builder().build(), Timeline.builder().build());
    String owner = UUID.randomUUID().toString();
    when(timelinesSpi.find(owner, 100)).thenReturn(expected);
    List<Timeline> actual = timelineService.search(owner);
    assertEquals(expected, actual);
  }
}
