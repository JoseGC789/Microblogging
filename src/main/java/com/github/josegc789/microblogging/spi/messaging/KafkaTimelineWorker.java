package com.github.josegc789.microblogging.spi.messaging;

import com.github.josegc789.microblogging.spi.TimelinesSpi;
import com.github.josegc789.microblogging.spi.entities.PublicationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaTimelineWorker {
  private final TimelinesSpi timelineService;

  @KafkaListener(
      topics = "${app.kafka.topic-publications}",
      groupId = "${spring.kafka.consumer.group-id}")
  public void handlePublication(PublicationEvent publication) {
    log.info("Handling publication event: {}", publication);
    timelineService.materialize(publication);
  }
}
