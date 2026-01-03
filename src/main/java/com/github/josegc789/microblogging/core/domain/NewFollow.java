package com.github.josegc789.microblogging.core.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record NewFollow(
    @NotBlank @Size(max = 40) String follower, @NotBlank @Size(max = 40) String followee) {}
