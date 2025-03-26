package com.RuanPablo2.mercadoapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MercadoAPI")
                        .version("1.0")
                        .description("API desenvolvida para gerenciar pedidos, estoque e pagamentos de um marketplace. "
                                + "Implementada com **Spring Boot**, segurança com **Spring Security** e **JWT**, persistência com **Spring Data JPA**. "
                                + "Inclui um fluxo completo de compras, desde a reserva de produtos no carrinho até a confirmação de pagamento e entrega."))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}