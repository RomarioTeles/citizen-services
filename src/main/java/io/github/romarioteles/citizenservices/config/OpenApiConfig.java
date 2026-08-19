package io.github.romarioteles.citizenservices.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Citizen Services API")
                        .description("API da plataforma de serviços digitais ao cidadão")
                        .version("0.0.1-SNAPSHOT"));
    }
}
