package com.github.josegc789.microblogging.core.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInUser(@NotBlank @Size(max = 40) String username) {}
