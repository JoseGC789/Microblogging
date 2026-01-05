package com.github.josegc789.microblogging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableMongoAuditing
public class MicrobloggingApplication {
  public static void main(String[] args) {
    SpringApplication.run(MicrobloggingApplication.class, args);
  }
}
