package com.github.josegc789.microblogging.spi;

import com.github.josegc789.microblogging.core.domain.NewFollow;

import java.util.Optional;

public interface FollowersSpi {

  Optional<String> follow(NewFollow follow);
}
