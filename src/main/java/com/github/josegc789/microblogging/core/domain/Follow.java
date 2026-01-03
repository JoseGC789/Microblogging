package com.github.josegc789.microblogging.core.domain;

import lombok.Builder;

@Builder
public record Follow(String id, User follower, User followee) {}
