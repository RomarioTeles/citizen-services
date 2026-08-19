package io.github.romarioteles.citizenservices.service.repository;

import io.github.romarioteles.citizenservices.service.domain.CitizenService;
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
        CitizenService service = new CitizenService("Emissão de Segunda Via", "Solicitação de segunda via de documento");

        CitizenService saved = repository.save(service);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindById() {
        CitizenService service = new CitizenService("Consulta de Benefício", null);
        CitizenService saved = repository.save(service);

        Optional<CitizenService> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Consulta de Benefício");
        assertThat(found.get().isActive()).isTrue();
        assertThat(found.get().getDescription()).isNull();
    }

    @Test
    void shouldPersistAllFields() {
        CitizenService service = new CitizenService("Solicitação de Certidão", "Certidão de nascimento");

        CitizenService saved = repository.save(service);
        Optional<CitizenService> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        CitizenService retrieved = found.get();
        assertThat(retrieved.getName()).isEqualTo("Solicitação de Certidão");
        assertThat(retrieved.getDescription()).isEqualTo("Certidão de nascimento");
        assertThat(retrieved.isActive()).isTrue();
        assertThat(retrieved.getCreatedAt()).isNotNull();
        assertThat(retrieved.getUpdatedAt()).isNotNull();
    }
}
