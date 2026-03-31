package org.s3m.userservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String authUrl = "http://localhost:8080/realms/my-realm/protocol/openid-connect/auth";
        String tokenUrl = "http://localhost:8080/realms/my-realm/protocol/openid-connect/token";

        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("keycloak",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(new OAuthFlows()
                            .authorizationCode(new OAuthFlow()
                                .authorizationUrl(authUrl)
                                .tokenUrl(tokenUrl)
                                .scopes(new Scopes()
                                    .addString("openid", "openid")
                                    .addString("profile", "profile")
                                )
                            )
                        )
                )
            )
            .addSecurityItem(new SecurityRequirement().addList("keycloak"));
    }
}