package com.github.josegc789.microblogging.api;

import com.github.josegc789.microblogging.core.MicroBlogging;
import com.github.josegc789.microblogging.core.NewPost;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/microblogging")
public class MicroBloggingRestController {

  private final MicroBlogging microBlogging;

  @PostMapping("/posts")
  public ResponseEntity<NewPost> post(@RequestBody NewPost newPost) {
    return ResponseEntity.ok(microBlogging.post(newPost));
  }
}
