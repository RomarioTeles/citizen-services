package io.github.romarioteles.citizenservices.actuator;

import io.github.romarioteles.citizenservices.infrastructure.observability.CorrelationIdFilter;
import io.github.romarioteles.citizenservices.infrastructure.observability.HttpRequestLoggingFilter;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.ServerHttpObservationFilter;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ActuatorObservabilityTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CorrelationIdFilter correlationIdFilter;

    @Autowired
    private HttpRequestLoggingFilter httpRequestLoggingFilter;

    @Autowired
    private ObservationRegistry observationRegistry;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new ServerHttpObservationFilter(observationRegistry), correlationIdFilter, httpRequestLoggingFilter)
                .build();
    }

    @Test
    void shouldReturnHealthUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void shouldReturnLivenessUp() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void shouldReturnReadinessUpWhenDatabaseIsAvailable() throws Exception {
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void shouldExposeMetricsEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/metrics")
                        .with(httpBasic("dev", "dev-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").isArray());
    }

    @Test
    void shouldExposeHttpServerRequestsMetricAfterRequest() throws Exception {
        mockMvc.perform(get("/actuator/health"));

        mockMvc.perform(get("/actuator/metrics/http.server.requests")
                        .with(httpBasic("dev", "dev-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("http.server.requests"));
    }

    @Nested
    class PrometheusTests {

        @Test
        void shouldExposePrometheusEndpoint() throws Exception {
            mockMvc.perform(get("/actuator/prometheus")
                            .with(httpBasic("dev", "dev-password")))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith("text/plain"));
        }

        @Test
        void shouldContainJvmMetric() throws Exception {
            mockMvc.perform(get("/actuator/prometheus")
                            .with(httpBasic("dev", "dev-password")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("jvm_memory_used_bytes")));
        }

        @Test
        void shouldContainBusinessMetrics() throws Exception {
            mockMvc.perform(get("/actuator/prometheus")
                            .with(httpBasic("dev", "dev-password")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("citizen_services_registrations_total")))
                    .andExpect(content().string(containsString("citizen_services_consulted_total")))
                    .andExpect(content().string(containsString("citizen_services_not_found_total")));
        }
    }
}
