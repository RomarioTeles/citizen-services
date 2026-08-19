package io.github.romarioteles.citizenservices.service.api;

import io.github.romarioteles.citizenservices.service.domain.CitizenService;
import io.github.romarioteles.citizenservices.service.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ServiceControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ServiceRepository repository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void shouldCreateServiceAndReturn201() throws Exception {
        long countBefore = repository.count();

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Emissão de certidão",
                                  "description": "Solicitação de emissão de certidão"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Emissão de certidão"))
                .andExpect(jsonPath("$.description").value("Solicitação de emissão de certidão"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        assertThat(repository.count()).isEqualTo(countBefore + 1);
    }

    @Test
    void shouldReturnServiceWhenFoundById() throws Exception {
        CitizenService saved = repository.save(new CitizenService("Consulta de CPF", "Consulta situação do CPF"));

        mockMvc.perform(get("/api/v1/services/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Consulta de CPF"))
                .andExpect(jsonPath("$.description").value("Consulta situação do CPF"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }

    @Test
    void shouldReturn404WhenServiceNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/services/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "" }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenNameExceeds150Characters() throws Exception {
        String longName = "a".repeat(151);

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "%s" }
                                """.formatted(longName)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenDescriptionExceeds500Characters() throws Exception {
        String longDescription = "a".repeat(501);

        mockMvc.perform(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Serviço válido",
                                  "description": "%s"
                                }
                                """.formatted(longDescription)))
                .andExpect(status().isBadRequest());
    }

    @Nested
    class FindAllTests {

        @BeforeEach
        void prepareData() {
            repository.deleteAll();
            repository.save(new CitizenService("Serviço A", "Descrição A"));
            repository.save(new CitizenService("Serviço B", "Descrição B"));
            repository.save(new CitizenService("Serviço C", "Descrição C"));
            repository.save(new CitizenService("Serviço D", "Descrição D"));
            repository.save(new CitizenService("Serviço E", "Descrição E"));
        }

        @Test
        void shouldReturnFirstPage() throws Exception {
            mockMvc.perform(get("/api/v1/services").param("page", "0").param("size", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(2))
                    .andExpect(jsonPath("$.totalElements").value(5))
                    .andExpect(jsonPath("$.totalPages").value(3));
        }

        @Test
        void shouldReturnSecondPage() throws Exception {
            mockMvc.perform(get("/api/v1/services").param("page", "1").param("size", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.page").value(1))
                    .andExpect(jsonPath("$.size").value(2))
                    .andExpect(jsonPath("$.totalElements").value(5))
                    .andExpect(jsonPath("$.totalPages").value(3));
        }

        @Test
        void shouldReturnEmptyContentWhenNoRecords() throws Exception {
            repository.deleteAll();

            mockMvc.perform(get("/api/v1/services").param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0))
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.totalPages").value(0));
        }
    }

    @Nested
    class UpdateServiceTests {

        @Test
        void shouldUpdateServiceAndReturn200() throws Exception {
            CitizenService saved = repository.save(new CitizenService("Nome original", "Descrição original"));

            mockMvc.perform(put("/api/v1/services/{id}", saved.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "Nome atualizado",
                                      "description": "Descrição atualizada"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(saved.getId()))
                    .andExpect(jsonPath("$.name").value("Nome atualizado"))
                    .andExpect(jsonPath("$.description").value("Descrição atualizada"))
                    .andExpect(jsonPath("$.active").value(true))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.updatedAt").isNotEmpty());
        }

        @Test
        void shouldReturn404WhenUpdatingNonExistentService() throws Exception {
            mockMvc.perform(put("/api/v1/services/999999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "Qualquer nome",
                                      "description": "Qualquer descrição"
                                    }
                                    """))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturn400WhenUpdateRequestIsInvalid() throws Exception {
            CitizenService saved = repository.save(new CitizenService("Serviço", null));

            mockMvc.perform(put("/api/v1/services/{id}", saved.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    { "name": "" }
                                    """))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldNotChangeProtectedFields() throws Exception {
            CitizenService saved = repository.save(new CitizenService("Serviço", null));
            boolean active = saved.isActive();

            mockMvc.perform(put("/api/v1/services/{id}", saved.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "Nome novo",
                                      "description": "Descrição nova"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(saved.getId()))
                    .andExpect(jsonPath("$.active").value(active))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty());
        }
    }

    @Nested
    class ActivateServiceTests {

        @Test
        void shouldActivateInactiveServiceAndReturn200() throws Exception {
            CitizenService saved = repository.save(new CitizenService("Serviço inativo", null));
            saved.setActive(false);
            repository.save(saved);

            mockMvc.perform(patch("/api/v1/services/{id}/activate", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(saved.getId()))
                    .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        void shouldReturn404WhenActivatingNonExistentService() throws Exception {
            mockMvc.perform(patch("/api/v1/services/999999/activate"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldActivateAlreadyActiveService() throws Exception {
            CitizenService saved = repository.save(new CitizenService("Serviço ativo", null));

            mockMvc.perform(patch("/api/v1/services/{id}/activate", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.active").value(true));
        }
    }

    @Nested
    class DeleteServiceTests {

        @Test
        void shouldDeactivateServiceAndReturn204() throws Exception {
            CitizenService saved = repository.save(new CitizenService("Serviço para desativar", null));

            mockMvc.perform(delete("/api/v1/services/{id}", saved.getId()))
                    .andExpect(status().isNoContent());

            CitizenService updated = repository.findById(saved.getId()).orElseThrow();
            assertThat(updated.isActive()).isFalse();
        }

        @Test
        void shouldKeepRecordInDatabaseAfterDelete() throws Exception {
            CitizenService saved = repository.save(new CitizenService("Serviço persistido", null));

            mockMvc.perform(delete("/api/v1/services/{id}", saved.getId()))
                    .andExpect(status().isNoContent());

            assertThat(repository.findById(saved.getId())).isPresent();
        }

        @Test
        void shouldReturn404WhenDeletingNonExistentService() throws Exception {
            mockMvc.perform(delete("/api/v1/services/999999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnActiveAsFalseOnGetAfterDelete() throws Exception {
            CitizenService saved = repository.save(new CitizenService("Serviço", "Descrição"));

            mockMvc.perform(delete("/api/v1/services/{id}", saved.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/v1/services/{id}", saved.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(saved.getId()))
                    .andExpect(jsonPath("$.active").value(false));
        }
    }
}
