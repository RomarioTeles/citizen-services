package io.github.romarioteles.citizenservices.service.api;

import io.github.romarioteles.citizenservices.service.api.dto.CreateServiceRequest;
import io.github.romarioteles.citizenservices.service.api.dto.ServicePageResponse;
import io.github.romarioteles.citizenservices.service.api.dto.ServiceResponse;
import io.github.romarioteles.citizenservices.service.api.dto.UpdateServiceRequest;
import io.github.romarioteles.citizenservices.service.application.ServiceApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final ServiceApplicationService applicationService;

    public ServiceController(ServiceApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceResponse create(@Valid @RequestBody CreateServiceRequest request) {
        return applicationService.create(request);
    }

    @GetMapping
    public ServicePageResponse findAll(Pageable pageable) {
        return applicationService.findAll(pageable);
    }

    @PutMapping("/{id}")
    public ServiceResponse update(@PathVariable Long id, @Valid @RequestBody UpdateServiceRequest request) {
        return applicationService.update(id, request);
    }

    @PatchMapping("/{id}/activate")
    public ServiceResponse activate(@PathVariable Long id) {
        return applicationService.activate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        applicationService.deactivate(id);
    }

    @GetMapping("/{id}")
    public ServiceResponse findById(@PathVariable Long id) {
        return applicationService.findById(id);
    }
}
