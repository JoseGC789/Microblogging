package com.github.josegc789.microblogging.core.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record ExistingPublication(@NotBlank String id, @NotBlank String owner) {

  public static ExistingPublication from(String owner, String id) {
    return ExistingPublication.builder().owner(owner).id(id).build();
  }
}
