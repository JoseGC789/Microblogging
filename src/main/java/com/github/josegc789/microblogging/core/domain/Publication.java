package com.github.josegc789.microblogging.core.domain;

import lombok.Builder;
import lombok.With;

@With
@Builder
public record Publication(String id, String owner, String content) {}
