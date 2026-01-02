package com.github.josegc789.microblogging.api;

import com.github.josegc789.microblogging.core.MicroBlogging;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class HelloWorldController {

  private final MicroBlogging helloWorld;

  @GetMapping("/hello")
  public ResponseEntity<String> helloWorld() {
    return ResponseEntity.ok("Hello world " + UUID.randomUUID());
  }
}
