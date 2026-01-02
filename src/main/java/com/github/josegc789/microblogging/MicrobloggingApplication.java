package com.github.josegc789.microblogging;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class MicrobloggingApplication {

  public static void main(String[] args) {
    SpringApplication.run(MicrobloggingApplication.class, args);
  }
}
