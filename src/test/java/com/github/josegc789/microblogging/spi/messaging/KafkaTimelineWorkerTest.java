package com.github.josegc789.microblogging.spi.messaging;

import com.github.josegc789.microblogging.spi.TimelinesSpi;
import com.github.josegc789.microblogging.spi.entities.PublicationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaTimelineWorkerTest {
  @Mock TimelinesSpi timelineService;
  @InjectMocks KafkaTimelineWorker worker;

  @Test
  void testShouldHandlePublication() {
    PublicationEvent pv = PublicationEvent.builder().build();
    worker.handlePublication(pv);
    verify(timelineService).materialize(pv);
  }
}
