package io.github.romarioteles.citizenservices.service.api.dto;

import io.github.romarioteles.citizenservices.service.domain.CitizenService;
import org.springframework.data.domain.Page;

import java.util.List;

public record ServicePageResponse(
        List<ServiceResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static ServicePageResponse from(Page<CitizenService> pageResult) {
        return new ServicePageResponse(
                pageResult.getContent().stream().map(ServiceResponse::from).toList(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }
}
