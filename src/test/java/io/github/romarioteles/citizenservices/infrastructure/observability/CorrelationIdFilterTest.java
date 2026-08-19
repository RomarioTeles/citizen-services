package io.github.romarioteles.citizenservices.infrastructure.observability;

import io.github.romarioteles.citizenservices.service.domain.CitizenService;
import io.github.romarioteles.citizenservices.service.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CorrelationIdFilterTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ServiceRepository repository;

    @Autowired
    private CorrelationIdFilter correlationIdFilter;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(correlationIdFilter)
                .build();
    }

    @Test
    void shouldPropagateCorrelationIdFromRequest() throws Exception {
        CitizenService saved = repository.save(new CitizenService("Serviço teste", null));

        mockMvc.perform(get("/api/v1/services/{id}", saved.getId())
                        .header(CorrelationIdFilter.HEADER, "test-correlation-id"))
                .andExpect(status().isOk())
                .andExpect(header().string(CorrelationIdFilter.HEADER, "test-correlation-id"));
    }

    @Test
    void shouldGenerateCorrelationIdWhenNotProvided() throws Exception {
        CitizenService saved = repository.save(new CitizenService("Serviço sem correlation", null));

        mockMvc.perform(get("/api/v1/services/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(header().exists(CorrelationIdFilter.HEADER));
    }

    @Test
    void shouldPropagateCorrelationIdOn404() throws Exception {
        mockMvc.perform(get("/api/v1/services/999999")
                        .header(CorrelationIdFilter.HEADER, "test-404-id"))
                .andExpect(status().isNotFound())
                .andExpect(header().string(CorrelationIdFilter.HEADER, "test-404-id"));
    }

    @Test
    void shouldPropagateCorrelationIdOn400() throws Exception {
        mockMvc.perform(post("/api/v1/services")
                        .header(CorrelationIdFilter.HEADER, "test-400-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(CorrelationIdFilter.HEADER, "test-400-id"));
    }
}
