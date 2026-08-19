package io.github.romarioteles.citizenservices.service.repository;

import io.github.romarioteles.citizenservices.service.domain.Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ServiceRepository.
 *
 * These tests require a running PostgreSQL instance on localhost:5432.
 */
@SpringBootTest
class ServiceRepositoryTest {

    @Autowired
    private ServiceRepository repository;

    @Test
    void shouldSaveAndRetrieveService() {
        Service service = new Service("Emissão de Segunda Via", "Solicitação de segunda via de documento");

        Service saved = repository.save(service);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindById() {
        Service service = new Service("Consulta de Benefício", null);
        Service saved = repository.save(service);

        Optional<Service> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Consulta de Benefício");
        assertThat(found.get().isActive()).isTrue();
        assertThat(found.get().getDescription()).isNull();
    }

    @Test
    void shouldPersistAllFields() {
        Service service = new Service("Solicitação de Certidão", "Certidão de nascimento");

        Service saved = repository.save(service);
        Optional<Service> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        Service retrieved = found.get();
        assertThat(retrieved.getName()).isEqualTo("Solicitação de Certidão");
        assertThat(retrieved.getDescription()).isEqualTo("Certidão de nascimento");
        assertThat(retrieved.isActive()).isTrue();
        assertThat(retrieved.getCreatedAt()).isNotNull();
        assertThat(retrieved.getUpdatedAt()).isNotNull();
    }
}
