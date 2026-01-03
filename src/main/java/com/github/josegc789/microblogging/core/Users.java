package com.github.josegc789.microblogging.core;

import com.github.josegc789.microblogging.core.domain.SignInUser;
import com.github.josegc789.microblogging.core.domain.User;

public interface Users {
  User signIn(SignInUser user);

  User search(String Id);
}
