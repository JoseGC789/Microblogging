package com.github.josegc789.microblogging.core.services;

import com.github.josegc789.microblogging.core.Timelines;
import com.github.josegc789.microblogging.core.domain.Timeline;
import com.github.josegc789.microblogging.spi.TimelinesSpi;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimelineService implements Timelines {
  private final TimelinesSpi timelinesSpi;

  @Override
  public List<Timeline> search(String owner, Instant date) {
    return timelinesSpi.find(owner, 10, date);
  }

  @Override
  public List<Timeline> search(String owner) {
    return timelinesSpi.find(owner, 10);
  }
}
