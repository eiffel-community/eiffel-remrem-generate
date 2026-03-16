package com.ericsson.eiffel.remrem.generate.config;

import com.ericsson.eiffel.remrem.generate.constants.RemremGenerateServiceConstants;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {


    @Bean
    public OpenAPI customOpenAPI() {
        // TODO get the application somehow both from spring-boot:run goal and when executed from jar/war artifact.
        String appVersion = "x.x.x";

        final StringBuilder remremDescription = new StringBuilder();
        remremDescription.append("REMReM (REST Mailbox for Registered Messages) Generate "
                + "for generating validated Eiffel messages.\n");
        remremDescription.append("<a href= " + RemremGenerateServiceConstants.DOCUMENTATION_URL + ">REMReM Generate documentation</a>");

        return new OpenAPI()
                .info(new Info()
                        .title("Eiffel REMReM Generate Service")
                        .description(remremDescription.toString())
                        .version(appVersion))
                .openapi("3.1.0");
    }

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("remrem-generate")
                .pathsToMatch("/**")
                .build();
    }
}
