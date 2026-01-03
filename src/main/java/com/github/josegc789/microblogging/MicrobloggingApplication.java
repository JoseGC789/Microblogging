package com.github.josegc789.microblogging;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@RequiredArgsConstructor
@EnableMongoAuditing
public class MicrobloggingApplication {

  static void main(String[] args) {
    SpringApplication.run(MicrobloggingApplication.class, args);
  }
}
