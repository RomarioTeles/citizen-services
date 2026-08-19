package io.github.romarioteles.citizenservices.service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateServiceRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 500)
        String description
) {
}
