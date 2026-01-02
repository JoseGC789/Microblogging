package com.github.josegc789.microblogging.spi;

import com.github.josegc789.microblogging.core.domain.ExistingPublication;
import com.github.josegc789.microblogging.core.domain.NewPublication;

public interface PostSpi {

  String publish(NewPublication newPublication);

  void unpublish(ExistingPublication toDelete);
}
