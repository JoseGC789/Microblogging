package com.github.josegc789.microblogging;

import com.github.josegc789.microblogging.spi.configuration.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@ConfigurationPropertiesScan
@RequiredArgsConstructor
@EnableMongoAuditing
@Slf4j
public class MicrobloggingApplication implements CommandLineRunner {
  private final AppProperties properties;

  public static void main(String[] args) {
    SpringApplication.run(MicrobloggingApplication.class, args);
  }

  @Override
  public void run(String... args) {
    log.info("Diagnostics...");
    log.info("Properties Binding for Kafka {}", properties.kafka());
    log.info("Properties Binding for Timeline {}", properties.timeline());
  }
}
