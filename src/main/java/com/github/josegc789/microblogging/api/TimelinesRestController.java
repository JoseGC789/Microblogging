package com.github.josegc789.microblogging.api;

import com.github.josegc789.microblogging.core.Timelines;
import com.github.josegc789.microblogging.core.domain.Timeline;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.requireNonNullElseGet;

@RestController
@RequiredArgsConstructor
@RequestMapping("/microblogging")
public class TimelinesRestController {

  private final Timelines timelines;

  @GetMapping("/timelines")
  public ResponseEntity<List<Timeline>> getTimeline(
      @RequestParam("owner") String owner,
      @RequestParam(value = "cursor", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          ZonedDateTime cursor) {
    return ResponseEntity.ok(
        timelines.search(owner, requireNonNullElseGet(cursor, ZonedDateTime::now).toInstant()));
  }
}
