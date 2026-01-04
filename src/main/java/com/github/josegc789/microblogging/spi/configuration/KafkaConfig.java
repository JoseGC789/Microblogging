package com.github.josegc789.microblogging.spi.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

  private final AppProperties properties;

  @Bean
  public NewTopic topic() {
    return TopicBuilder.name(properties.kafka().topicPublications())
        .partitions(1)
        .replicas(1)
        .build();
  }
}
