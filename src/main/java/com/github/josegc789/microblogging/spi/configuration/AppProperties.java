package com.github.josegc789.microblogging.spi.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Kafka kafka, Timeline timeline) {
  public record Kafka(String topicPublications, String topicPublicationDeletes) {}

  public record Timeline(int defaultLimit, int maxLimit) {}
}
