package io.github.romarioteles.citizenservices.api.exception;

import io.github.romarioteles.citizenservices.service.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class GlobalExceptionHandlerTest {

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

    private static MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder builder) {
        return builder.with(httpBasic("dev", "dev-password"));
    }

    private static MockHttpServletRequestBuilder authAdmin(MockHttpServletRequestBuilder builder) {
        return builder.with(httpBasic("admin", "admin-password"));
    }

    @Test
    void shouldReturn404WithErrorContractWhenServiceNotFound() throws Exception {
        mockMvc.perform(auth(get("/api/v1/services/999999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/api/v1/services/999999"));
    }

    @Test
    void shouldReturn400WithFieldErrorsWhenPostRequestIsInvalid() throws Exception {
        mockMvc.perform(authAdmin(post("/api/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "" }
                                """)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("Dados da requisição inválidos"))
                .andExpect(jsonPath("$.path").value("/api/v1/services"))
                .andExpect(jsonPath("$.fieldErrors.name").isNotEmpty());
    }

    @Test
    void shouldReturn404WithErrorContractWhenPuttingNonExistentService() throws Exception {
        mockMvc.perform(authAdmin(put("/api/v1/services/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Qualquer nome",
                                  "description": "Qualquer descrição"
                                }
                                """)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/v1/services/999999"));
    }

    @Test
    void shouldReturn404WithErrorContractWhenDeletingNonExistentService() throws Exception {
        mockMvc.perform(authAdmin(delete("/api/v1/services/999999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/v1/services/999999"));
    }

    @Test
    void shouldReturn404WithErrorContractWhenActivatingNonExistentService() throws Exception {
        mockMvc.perform(authAdmin(patch("/api/v1/services/999999/activate")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/v1/services/999999/activate"));
    }
}
