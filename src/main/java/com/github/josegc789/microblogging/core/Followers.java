package com.github.josegc789.microblogging.core;

import com.github.josegc789.microblogging.core.domain.Follow;
import com.github.josegc789.microblogging.core.domain.NewFollow;

public interface Followers {
  Follow follow(NewFollow follow);
}
