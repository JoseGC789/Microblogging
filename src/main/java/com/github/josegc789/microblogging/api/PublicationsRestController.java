package com.github.josegc789.microblogging.api;

import com.github.josegc789.microblogging.core.MicroBlogging;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.core.domain.Publication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/microblogging")
public class PublicationsRestController {

  private final MicroBlogging microBlogging;

  @PostMapping("/publications")
  public ResponseEntity<Publication> publish(@RequestBody NewPublication newPublication) {
    return ResponseEntity.ok(microBlogging.publish(newPublication));
  }

  @DeleteMapping("/{owner}/publications/{id}")
  public ResponseEntity<Void> delete(
      @PathVariable("owner") String owner, @PathVariable("id") String id) {
    microBlogging.unpublish(owner, id);
    return ResponseEntity.noContent().build();
  }
}
