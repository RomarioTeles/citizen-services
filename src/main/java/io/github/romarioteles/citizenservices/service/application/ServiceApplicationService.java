package io.github.romarioteles.citizenservices.service.application;

import io.github.romarioteles.citizenservices.service.api.dto.CreateServiceRequest;
import io.github.romarioteles.citizenservices.service.api.dto.ServicePageResponse;
import io.github.romarioteles.citizenservices.service.api.dto.ServiceResponse;
import io.github.romarioteles.citizenservices.service.api.dto.UpdateServiceRequest;
import io.github.romarioteles.citizenservices.service.application.exception.ServiceNotFoundException;
import io.github.romarioteles.citizenservices.service.application.metrics.ServiceMetrics;
import io.github.romarioteles.citizenservices.service.domain.CitizenService;
import io.github.romarioteles.citizenservices.service.repository.ServiceRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceApplicationService {

    private final ServiceRepository repository;
    private final ServiceMetrics metrics;

    public ServiceApplicationService(ServiceRepository repository, ServiceMetrics metrics) {
        this.repository = repository;
        this.metrics = metrics;
    }

    @Transactional
    public ServiceResponse create(CreateServiceRequest request) {
        CitizenService service = new CitizenService(request.name(), request.description());
        ServiceResponse response = ServiceResponse.from(repository.save(service));
        metrics.incrementCreated();
        return response;
    }

    @Transactional(readOnly = true)
    public ServiceResponse findById(Long id) {
        return repository.findById(id)
                .map(entity -> {
                    metrics.incrementConsulted();
                    return ServiceResponse.from(entity);
                })
                .orElseThrow(() -> {
                    metrics.incrementNotFound();
                    return new ServiceNotFoundException(id);
                });
    }

    @Transactional(readOnly = true)
    public ServicePageResponse findAll(Pageable pageable) {
        return ServicePageResponse.from(repository.findAll(pageable));
    }

    @Transactional
    public ServiceResponse update(Long id, UpdateServiceRequest request) {
        CitizenService service = repository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));
        service.setName(request.name());
        service.setDescription(request.description());
        return ServiceResponse.from(repository.save(service));
    }

    @Transactional
    public void deactivate(Long id) {
        CitizenService service = repository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));
        service.setActive(false);
        repository.save(service);
    }

    @Transactional
    public ServiceResponse activate(Long id) {
        CitizenService service = repository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));
        service.setActive(true);
        return ServiceResponse.from(repository.save(service));
    }
}
