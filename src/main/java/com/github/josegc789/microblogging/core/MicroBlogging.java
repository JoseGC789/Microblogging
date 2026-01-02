package com.github.josegc789.microblogging.core;

import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.core.domain.Publication;

public interface MicroBlogging {
  Publication publish(NewPublication content);

  void unpublish(String owner, String id);
}
