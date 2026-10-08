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
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceApplicationServiceTest {

    @Mock
    private ServiceRepository repository;

    @Mock
    private ServiceMetrics metrics;

    @Mock
    private Tracer tracer;

    @Mock
    private SpanBuilder spanBuilder;

    @Mock
    private Span span;

    @InjectMocks
    private ServiceApplicationService service;

    @Test
    void shouldCreateServiceSuccessfully() {
        CreateServiceRequest request = new CreateServiceRequest(
                "Emissão de RG",
                "Solicitação de emissão de documento"
        );

        when(tracer.spanBuilder("service.create")).thenReturn(spanBuilder);
        when(spanBuilder.startSpan()).thenReturn(span);
        when(span.makeCurrent()).thenReturn(mock(Scope.class));

        when(repository.save(any(CitizenService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = service.create(request);

        assertNotNull(response);
        assertEquals("Emissão de RG", response.name());
        assertEquals(
                "Solicitação de emissão de documento",
                response.description()
        );
        assertTrue(response.active());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());

        verify(repository).save(any(CitizenService.class));
        verify(metrics).incrementCreated();
        verify(span).setAttribute("service.name", "Emissão de RG");
        verify(span).end();
    }

    @Test
    void shouldPropagateExceptionWhenRepositoryFails() {
        CreateServiceRequest request = new CreateServiceRequest(
                "Emissão de RG",
                "Solicitação de emissão de documento"
        );

        RuntimeException exception =
                new RuntimeException("Database unavailable");

        when(tracer.spanBuilder("service.create")).thenReturn(spanBuilder);
        when(spanBuilder.startSpan()).thenReturn(span);
        when(span.makeCurrent()).thenReturn(mock(Scope.class));

        when(repository.save(any(CitizenService.class)))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> service.create(request)
        );

        assertSame(exception, thrown);
        verify(metrics, never()).incrementCreated();
        verify(span).recordException(exception);
        verify(span).setStatus(StatusCode.ERROR);
        verify(span).end();
    }

    @Test
    void shouldFindServiceByIdSuccessfully() {
        Long id = 1L;

        CitizenService existingService = new CitizenService(
                "Emissão de RG",
                "Solicitação de emissão de documento"
        );

        when(repository.findById(id))
                .thenReturn(Optional.of(existingService));

        ServiceResponse response = service.findById(id);

        assertNotNull(response);
        assertEquals("Emissão de RG", response.name());
        assertEquals(
                "Solicitação de emissão de documento",
                response.description()
        );
        assertTrue(response.active());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());

        verify(repository).findById(id);
        verify(metrics).incrementConsulted();
        verify(metrics, never()).incrementNotFound();
    }

    @Test
    void shouldThrowServiceNotFoundExceptionWhenServiceDoesNotExist() {
        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        ServiceNotFoundException exception = assertThrows(
                ServiceNotFoundException.class,
                () -> service.findById(id)
        );

        assertEquals("Service not found with id: 999", exception.getMessage());

        verify(metrics).incrementNotFound();
        verify(metrics, never()).incrementConsulted();
        verify(repository).findById(id);
    }

    @Test
    void shouldFindAllServicesSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);

        CitizenService service1 = new CitizenService(
                "Emissão de RG",
                "Solicitação de emissão de documento"
        );

        CitizenService service2 = new CitizenService(
                "Segunda via de CPF",
                "Solicitação de segunda via de CPF"
        );

        Page<CitizenService> page = new PageImpl<>(
                List.of(service1, service2),
                pageable,
                2
        );

        when(repository.findAll(pageable)).thenReturn(page);

        ServicePageResponse response = service.findAll(pageable);

        assertNotNull(response);
        assertEquals(2, response.content().size());

        assertEquals("Emissão de RG", response.content().get(0).name());
        assertEquals(
                "Solicitação de emissão de documento",
                response.content().get(0).description()
        );

        assertEquals("Segunda via de CPF", response.content().get(1).name());
        assertEquals(
                "Solicitação de segunda via de CPF",
                response.content().get(1).description()
        );

        assertEquals(0, response.page());
        assertEquals(10, response.size());
        assertEquals(2, response.totalElements());
        assertEquals(1, response.totalPages());

        verify(repository).findAll(pageable);
    }

    @Test
    void shouldUpdateServiceSuccessfully() {
        Long id = 1L;

        CitizenService existingService = new CitizenService(
                "Emissão de RG",
                "Descrição antiga"
        );

        UpdateServiceRequest request = new UpdateServiceRequest(
                "Emissão de RG Digital",
                "Nova descrição do serviço"
        );

        when(repository.findById(id))
                .thenReturn(Optional.of(existingService));

        when(repository.save(any(CitizenService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Instant originalUpdatedAt = existingService.getUpdatedAt();

        ServiceResponse response = service.update(id, request);

        assertNotNull(response);
        assertEquals("Emissão de RG Digital", response.name());
        assertEquals("Nova descrição do serviço", response.description());
        assertTrue(response.active());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());
        assertNotEquals(originalUpdatedAt, response.updatedAt());

        verify(repository).findById(id);
        verify(repository).save(existingService);
    }

    @Test
    void shouldThrowServiceNotFoundExceptionWhenUpdatingNonExistingService() {
        Long id = 999L;

        UpdateServiceRequest request = new UpdateServiceRequest(
                "Emissão de RG Digital",
                "Nova descrição do serviço"
        );

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        ServiceNotFoundException exception = assertThrows(
                ServiceNotFoundException.class,
                () -> service.update(id, request)
        );

        assertEquals("Service not found with id: 999", exception.getMessage());

        verify(repository).findById(id);
        verify(repository, never()).save(any(CitizenService.class));
    }

    @Test
    void shouldDeactivateServiceSuccessfully() {
        Long id = 1L;

        CitizenService existingService = new CitizenService(
                "Emissão de RG",
                "Solicitação de emissão de documento"
        );

        when(repository.findById(id))
                .thenReturn(Optional.of(existingService));

        when(repository.save(any(CitizenService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.deactivate(id);

        assertFalse(existingService.isActive());

        verify(repository).findById(id);
        verify(repository).save(existingService);
    }

    @Test
    void shouldThrowServiceNotFoundExceptionWhenDeactivatingNonExistingService() {
        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ServiceNotFoundException.class,
                () -> service.deactivate(id)
        );

        verify(repository).findById(id);
        verify(repository, never()).save(any(CitizenService.class));
    }

    @Test
    void shouldActivateServiceSuccessfully() {
        Long id = 1L;

        CitizenService existingService = new CitizenService(
                "Emissão de RG",
                "Solicitação de emissão de documento"
        );

        existingService.setActive(false);

        when(repository.findById(id))
                .thenReturn(Optional.of(existingService));

        when(repository.save(any(CitizenService.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = service.activate(id);

        assertNotNull(response);
        assertEquals("Emissão de RG", response.name());
        assertEquals(
                "Solicitação de emissão de documento",
                response.description()
        );
        assertTrue(response.active());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());

        verify(repository).findById(id);
        verify(repository).save(existingService);

        assertTrue(existingService.isActive());
    }

    @Test
    void shouldThrowServiceNotFoundExceptionWhenActivatingNonExistingService() {
        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ServiceNotFoundException.class,
                () -> service.activate(id)
        );

        verify(repository).findById(id);
        verify(repository, never()).save(any(CitizenService.class));
    }
}
