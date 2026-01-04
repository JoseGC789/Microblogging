package com.github.josegc789.microblogging.spi;

import com.github.josegc789.microblogging.core.domain.Follow;
import com.github.josegc789.microblogging.core.domain.Timeline;
import com.github.josegc789.microblogging.spi.entities.PublicationEvent;
import org.springframework.scheduling.annotation.Async;

import java.time.Instant;
import java.util.List;

public interface TimelinesSpi {

  @Async("virtualThreadExecutor")
  void materialize(PublicationEvent publication);

  @Async("virtualThreadExecutor")
  void materialize(Follow follow);

  List<Timeline> find(String owner, int limit);

  List<Timeline> find(String owner, int limit, Instant cursor);
}
