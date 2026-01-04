package com.github.josegc789.microblogging.spi.entities;

import lombok.Builder;

import java.time.Instant;

@Builder
public record PublicationEvent(
    String id, String authorUsername, String authorId, String content, Instant createdOn) {}
