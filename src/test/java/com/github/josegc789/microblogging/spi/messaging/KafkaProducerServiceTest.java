package com.github.josegc789.microblogging.spi.messaging;

import com.github.josegc789.microblogging.spi.configuration.AppProperties;
import com.github.josegc789.microblogging.spi.entities.PublicationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

  @Mock KafkaTemplate<String, PublicationEvent> kafkaTemplate;
  @Mock AppProperties props;
  @InjectMocks KafkaProducerService kafkaProducerService;

  @Test
  void testShouldSend() {
    PublicationEvent ev = PublicationEvent.builder().build();
    AppProperties.Kafka kafka =
        new AppProperties.Kafka(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    when(props.kafka()).thenReturn(kafka);
    when(kafkaTemplate.send(kafka.topicPublications(), ev))
        .thenReturn(CompletableFuture.failedFuture(new Exception()));
    kafkaProducerService.send(ev);
  }
}
