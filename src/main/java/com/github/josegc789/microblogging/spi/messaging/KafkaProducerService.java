package com.github.josegc789.microblogging.spi.messaging;

import com.github.josegc789.microblogging.spi.configuration.AppProperties;
import com.github.josegc789.microblogging.spi.entities.PublicationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
  private final KafkaTemplate<String, PublicationEvent> kafkaTemplate;
  private final AppProperties props;

  public void send(PublicationEvent ev) {
    kafkaTemplate.send(props.kafka().topicPublications(), ev);
  }
}
