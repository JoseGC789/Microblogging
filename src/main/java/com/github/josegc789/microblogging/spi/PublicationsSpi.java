package com.github.josegc789.microblogging.spi;

import com.github.josegc789.microblogging.core.domain.ExistingPublication;
import com.github.josegc789.microblogging.core.domain.NewPublication;

public interface PublicationsSpi {

  String create(NewPublication newPublication);

  void delete(ExistingPublication toDelete);
}
