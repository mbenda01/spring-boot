package iibs.gestionportefeuille.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gestionPortefeuilleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de gestion de portefeuilles")
                        .description("Gestion des utilisateurs, portefeuilles et transactions")
                        .version("1.0.0")
                        .contact(new Contact().name("IIBS")));
    }
}