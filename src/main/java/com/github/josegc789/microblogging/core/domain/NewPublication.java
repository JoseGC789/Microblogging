package com.github.josegc789.microblogging.core.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record NewPublication(@NotBlank String authorId, @NotBlank @Size(max = 280) String content) {}
