package com.github.josegc789.microblogging.spi;

import com.github.josegc789.microblogging.core.domain.Timeline;
import com.github.josegc789.microblogging.spi.entities.PublicationDocument;
import java.time.Instant;
import java.util.List;

public interface TimelinesSpi {

  void distribute(PublicationDocument document);

  List<Timeline> find(String owner, int limit);

  List<Timeline> find(String owner, int limit, Instant cursor);
}
