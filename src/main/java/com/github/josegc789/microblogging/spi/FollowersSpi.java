package com.github.josegc789.microblogging.spi;

import com.github.josegc789.microblogging.core.domain.NewFollow;
import java.util.List;
import java.util.Optional;

public interface FollowersSpi {

  Optional<String> follow(NewFollow follow);

  List<String> followersOf(String followee);

  List<String> followeesOf(String follower);
}
