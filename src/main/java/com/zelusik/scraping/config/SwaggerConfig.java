package com.zelusik.scraping.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI eateryApi(@Value("${eatery.scraping-server.version}") String docVersion) {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Eatery Scraping Server API Docs")
                                .description("Eatery의 Scraping Server API 명세서")
                                .version(docVersion)
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("Github organization of team zelusik")
                                .url("https://github.com/Zelusik")
                );
    }
}
