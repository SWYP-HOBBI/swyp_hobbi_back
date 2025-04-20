package swyp.hobbi.swyphobbiback.common.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi userGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/api/v1/user/**")
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                        new Info()
                                                .title("user api")
                                                .description("유저 관련 API")
                                                .version("0.0.1")
                                )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi postGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("post")
                .pathsToMatch("/api/v1/post/**")
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                        new Info()
                                                .title("post api")
                                                .description("게시글 관련 API")
                                                .version("0.0.1")
                                )
                )
                .build();
    }
}
