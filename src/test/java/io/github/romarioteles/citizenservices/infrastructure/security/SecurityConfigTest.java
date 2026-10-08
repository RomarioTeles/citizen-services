package io.github.romarioteles.citizenservices.infrastructure.security;

import io.github.romarioteles.citizenservices.TestcontainersConfiguration;
import io.github.romarioteles.citizenservices.service.domain.CitizenService;
import io.github.romarioteles.citizenservices.service.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ServiceRepository repository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldReturn401WhenGetServiceWithoutAuth() throws Exception {
        CitizenService saved = repository.save(new CitizenService("Serviço Seguro", null));

        mockMvc.perform(get("/api/v1/services/{id}", saved.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenGetServiceWithInvalidCredentials() throws Exception {
        CitizenService saved = repository.save(new CitizenService("Serviço Inválido", null));

        mockMvc.perform(get("/api/v1/services/{id}", saved.getId())
                        .with(httpBasic("dev", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenPostServiceWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/v1/services")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                { "name": "Serviço sem auth" }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn200ForActuatorHealthWithoutAuth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401ForActuatorInfoWithoutAuth() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn200ForSwaggerWithoutAuth() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401ForUnknownEndpointWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/unknown"))
                .andExpect(status().isUnauthorized());
    }

    @Nested
    class UserRoleTests {

        @Test
        void shouldReturn200WhenUserGetsService() throws Exception {
            CitizenService saved = repository.save(new CitizenService("Serviço USER", null));

            mockMvc.perform(get("/api/v1/services/{id}", saved.getId())
                            .with(httpBasic("dev", "dev-password")))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldReturn403WhenUserCreatesService() throws Exception {
            mockMvc.perform(post("/api/v1/services")
                            .with(httpBasic("dev", "dev-password"))
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    { "name": "Serviço USER tentando criar" }
                                    """))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class AdminRoleTests {

        @Test
        void shouldReturn200WhenAdminGetsService() throws Exception {
            CitizenService saved = repository.save(new CitizenService("Serviço ADMIN", null));

            mockMvc.perform(get("/api/v1/services/{id}", saved.getId())
                            .with(httpBasic("admin", "admin-password")))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldReturn201WhenAdminCreatesService() throws Exception {
            mockMvc.perform(post("/api/v1/services")
                            .with(httpBasic("admin", "admin-password"))
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    { "name": "Serviço criado pelo ADMIN" }
                                    """))
                    .andExpect(status().isCreated());
        }
    }
}
