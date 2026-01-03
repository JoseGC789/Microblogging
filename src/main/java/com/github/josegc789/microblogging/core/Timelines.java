package com.github.josegc789.microblogging.core;

import com.github.josegc789.microblogging.core.domain.Timeline;

import java.time.Instant;
import java.util.List;

public interface Timelines {
  List<Timeline> search(String owner, Instant date);

  List<Timeline> search(String owner);
}
