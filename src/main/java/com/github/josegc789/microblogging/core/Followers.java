package com.github.josegc789.microblogging.core;

import com.github.josegc789.microblogging.core.domain.Follow;

public interface Followers {
  Follow follow(String follower, String followee);
}
