package com.github.josegc789.microblogging.core.domain;

import java.time.Instant;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record Timeline(
    String publicationId,
    String owner,
    String authorId,
    String authorUsername,
    String content,
    Instant createdOn) {}
