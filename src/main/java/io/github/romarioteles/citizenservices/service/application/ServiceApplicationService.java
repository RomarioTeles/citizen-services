package io.github.romarioteles.citizenservices.service.application;

import io.github.romarioteles.citizenservices.service.api.dto.CreateServiceRequest;
import io.github.romarioteles.citizenservices.service.api.dto.ServicePageResponse;
import io.github.romarioteles.citizenservices.service.api.dto.ServiceResponse;
import io.github.romarioteles.citizenservices.service.api.dto.UpdateServiceRequest;
import io.github.romarioteles.citizenservices.service.application.exception.ServiceNotFoundException;
import io.github.romarioteles.citizenservices.service.application.metrics.ServiceMetrics;
import io.github.romarioteles.citizenservices.service.domain.CitizenService;
import io.github.romarioteles.citizenservices.service.repository.ServiceRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceApplicationService {

    private final ServiceRepository repository;
    private final ServiceMetrics metrics;
    private final Tracer tracer;

    public ServiceApplicationService(ServiceRepository repository, ServiceMetrics metrics, Tracer tracer) {
        this.repository = repository;
        this.metrics = metrics;
        this.tracer = tracer;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ServiceResponse create(CreateServiceRequest request) {

        Span span = tracer.spanBuilder("service.create")
                .startSpan();

        span.setAttribute("service.name", request.name());

        try (var scope = span.makeCurrent()) {

            CitizenService service = new CitizenService(request.name(), request.description());

            ServiceResponse response = ServiceResponse.from(repository.save(service));

            metrics.incrementCreated();

            return response;

        } catch (Exception e) {

            span.recordException(e);
            span.setStatus(StatusCode.ERROR);

            throw e;

        } finally {
            span.end();
        }
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
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

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Transactional(readOnly = true)
    public ServicePageResponse findAll(Pageable pageable) {
        return ServicePageResponse.from(repository.findAll(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ServiceResponse update(Long id, UpdateServiceRequest request) {
        CitizenService service = repository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));
        service.setName(request.name());
        service.setDescription(request.description());
        return ServiceResponse.from(repository.save(service));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deactivate(Long id) {
        CitizenService service = repository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));
        service.setActive(false);
        repository.save(service);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ServiceResponse activate(Long id) {
        CitizenService service = repository.findById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));
        service.setActive(true);
        return ServiceResponse.from(repository.save(service));
    }
}
