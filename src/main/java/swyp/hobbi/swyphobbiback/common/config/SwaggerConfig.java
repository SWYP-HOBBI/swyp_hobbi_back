package swyp.hobbi.swyphobbiback.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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

    @Bean
    public GroupedOpenApi emailGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("email")
                .pathsToMatch("/api/v1/email/**")
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                        new Info()
                                                .title("email api")
                                                .description("이메일 관련 API")
                                                .version("0.0.1")
                                )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi authGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("token")
                .pathsToMatch("/api/v1/token/**")
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                        new Info()
                                                .title("token api")
                                                .description("토큰 관련 API")
                                                .version("0.0.1")
                                )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi mypageGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("mypage")
                .pathsToMatch("/api/v1/my-page/**")
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                        new Info()
                                                .title("mypage api")
                                                .description("마이페이지 관련 API")
                                                .version("0.0.1")
                                )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi notificationGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("notification")
                .pathsToMatch("/api/v1/notification/**")
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                        new Info()
                                                .title("notification api")
                                                .description("알림 관련 API")
                                                .version("0.0.1")
                                )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi commentGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("comment")
                .pathsToMatch("/api/v1/comment/**")
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                        new Info()
                                                .title("comment api")
                                                .description("댓글 관련 API")
                                                .version("0.0.1")
                                )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi likeGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("like")
                .pathsToMatch("/api/v1/like/**")
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                        new Info()
                                                .title("like api")
                                                .description("좋아요 관련 API")
                                                .version("0.0.1")
                                )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi oauthGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("oauth")
                .pathsToMatch("/api/v1/oauth/**")
                .addOpenApiCustomizer(
                        openApi ->
                                openApi.setInfo(
                                        new Info()
                                                .title("oauth api")
                                                .description("소셜로그인 관련 API")
                                                .version("0.0.1")
                                )
                )
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }


}
