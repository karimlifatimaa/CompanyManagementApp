package com.woofly.companymanagementapp.config;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Company Management ")
                        .version("0.0.1-SNAPSHOT ")
                        .description("Şirkət İdarəetmə Tətbiqinin API sənədləşdirilməsi")
                );
    }
}