package com.github.josegc789.microblogging.spi;

import com.github.josegc789.microblogging.core.domain.SignInUser;
import com.github.josegc789.microblogging.core.domain.User;
import com.github.josegc789.microblogging.spi.entities.UsersDocument;

import java.util.Optional;

public interface UsersSpi {

  Optional<String> create(SignInUser signInUser);

  Optional<User> find(String id);
}
