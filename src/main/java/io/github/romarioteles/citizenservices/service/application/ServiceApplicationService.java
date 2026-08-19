package io.github.romarioteles.citizenservices.service.application;

import io.github.romarioteles.citizenservices.service.api.dto.CreateServiceRequest;
import io.github.romarioteles.citizenservices.service.api.dto.ServiceResponse;
import io.github.romarioteles.citizenservices.service.domain.CitizenService;
import io.github.romarioteles.citizenservices.service.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceApplicationService {

    private final ServiceRepository repository;

    public ServiceApplicationService(ServiceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ServiceResponse create(CreateServiceRequest request) {
        CitizenService service = new CitizenService(request.name(), request.description());
        return ServiceResponse.from(repository.save(service));
    }
}
