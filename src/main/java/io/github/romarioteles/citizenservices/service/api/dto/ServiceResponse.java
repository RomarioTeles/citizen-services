package io.github.romarioteles.citizenservices.service.api.dto;

import io.github.romarioteles.citizenservices.service.domain.CitizenService;

import java.time.Instant;

public record ServiceResponse(
        Long id,
        String name,
        String description,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static ServiceResponse from(CitizenService service) {
        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.isActive(),
                service.getCreatedAt(),
                service.getUpdatedAt()
        );
    }
}
