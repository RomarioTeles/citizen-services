package io.github.romarioteles.citizenservices;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Shared Testcontainers configuration for integration tests.
 *
 * <p>Provides a single, reusable PostgreSQL 16 container that Spring Boot wires
 * automatically through {@link ServiceConnection}: the JDBC URL, username and
 * password are discovered from the container, so none of them need to be
 * declared in {@code application.yaml}. Flyway then runs its existing migrations
 * against this ephemeral database.</p>
 *
 * <p>Integration tests based on {@code @SpringBootTest} import this class to get
 * a real PostgreSQL without a manually started local instance. The container is
 * created and destroyed by the Testcontainers lifecycle.</p>
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer("postgres:16");
    }
}
