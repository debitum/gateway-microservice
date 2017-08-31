package com.debitum.gateway.port.adapter.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Configuration
@EnableSwagger2
class SwaggerConfiguration {

    @Bean
    public Docket swaggerConfig() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("Gateway service.")
                .description("Service to manage agents, advertisers and their integrations.")
                .version("1.0.0").build();

        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.basePackage("com.debitum")).paths(PathSelectors.ant("/**")).build().pathMapping("/")
                .apiInfo(apiInfo)
                .directModelSubstitute(LocalDate.class,
                        String.class)
                .directModelSubstitute(LocalDateTime.class,
                        String.class)
                .directModelSubstitute(ZonedDateTime.class,
                        String.class)
                .directModelSubstitute(Instant.class,
                        String.class)
                .useDefaultResponseMessages(false);
    }
}
