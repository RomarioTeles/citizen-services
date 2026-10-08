package io.github.romarioteles.citizenservices.service.application.metrics;

import io.github.romarioteles.citizenservices.service.api.dto.CreateServiceRequest;
import io.github.romarioteles.citizenservices.service.application.ServiceApplicationService;
import io.github.romarioteles.citizenservices.service.application.exception.ServiceNotFoundException;
import io.github.romarioteles.citizenservices.service.domain.CitizenService;
import io.github.romarioteles.citizenservices.TestcontainersConfiguration;
import io.github.romarioteles.citizenservices.service.repository.ServiceRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ServiceMetricsTest {

    @Autowired
    private ServiceApplicationService service;

    @Autowired
    private ServiceRepository repository;

    @Autowired
    private MeterRegistry registry;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldIncrementCreatedCounterOnCreate() {
        Counter counter = registry.counter("citizen.services.registrations");
        double before = counter.count();

        service.create(new CreateServiceRequest("Serviço Métricas Criação", null));

        assertThat(counter.count()).isEqualTo(before + 1);
    }

    @Test
    @WithMockUser
    void shouldIncrementConsultedCounterWhenServiceFound() {
        CitizenService saved = repository.save(new CitizenService("Serviço Métricas Consulta", null));
        Counter counter = registry.counter("citizen.services.consulted");
        double before = counter.count();

        service.findById(saved.getId());

        assertThat(counter.count()).isEqualTo(before + 1);
    }

    @Test
    @WithMockUser
    void shouldIncrementNotFoundCounterAndNotConsultedWhenServiceNotFound() {
        Counter notFound = registry.counter("citizen.services.not_found");
        Counter consulted = registry.counter("citizen.services.consulted");
        double notFoundBefore = notFound.count();
        double consultedBefore = consulted.count();

        assertThatThrownBy(() -> service.findById(999999L))
                .isInstanceOf(ServiceNotFoundException.class);

        assertThat(notFound.count()).isEqualTo(notFoundBefore + 1);
        assertThat(consulted.count()).isEqualTo(consultedBefore);
    }
}
