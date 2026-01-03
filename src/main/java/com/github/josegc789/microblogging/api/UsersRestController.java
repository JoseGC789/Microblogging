package com.github.josegc789.microblogging.api;

import com.github.josegc789.microblogging.core.Followers;
import com.github.josegc789.microblogging.core.Users;
import com.github.josegc789.microblogging.core.domain.Follow;
import com.github.josegc789.microblogging.core.domain.NewFollow;
import com.github.josegc789.microblogging.core.domain.SignInUser;
import com.github.josegc789.microblogging.core.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/microblogging")
public class UsersRestController {

  private final Users users;
  private final Followers followers;

  @PostMapping("/users")
  public ResponseEntity<User> signIn(@RequestBody SignInUser signInUser) {
    return ResponseEntity.ok(users.signIn(signInUser));
  }

  @PutMapping("/users/follow")
  public ResponseEntity<Follow> follow(@RequestBody NewFollow follow) {
    return ResponseEntity.ok(followers.follow(follow));
  }
}
