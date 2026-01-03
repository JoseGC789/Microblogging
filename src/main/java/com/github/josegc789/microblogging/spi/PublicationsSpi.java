package com.github.josegc789.microblogging.spi;

import com.github.josegc789.microblogging.core.domain.ExistingPublication;
import com.github.josegc789.microblogging.core.domain.NewPublication;
import com.github.josegc789.microblogging.core.domain.User;
import com.github.josegc789.microblogging.spi.entities.PublicationDocument;

import java.util.List;

public interface PublicationsSpi {

  String create(NewPublication newPublication, User user);

  List<PublicationDocument> find(List<String> publications);

  void delete(ExistingPublication toDelete);
}
