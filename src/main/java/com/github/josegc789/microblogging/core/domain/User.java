package com.github.josegc789.microblogging.core.domain;

import lombok.Builder;

@Builder
public record User(String id, String username) {}
