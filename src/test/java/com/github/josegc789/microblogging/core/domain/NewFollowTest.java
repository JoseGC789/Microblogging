package com.github.josegc789.microblogging.core.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

class NewFollowTest {
  @Test
  void testShouldThrowWhenFieldsAreTheSame() {
    String uuid = UUID.randomUUID().toString();
    assertThrows(
        BadFollowException.class, () -> NewFollow.builder().follower(uuid).followee(uuid).build());
  }
}
