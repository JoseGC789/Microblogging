package com.github.josegc789.microblogging.core;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
public record NewPost(
    String id, @NotBlank String owner, @NotBlank @Size(max = 280) String content) {}
