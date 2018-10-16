package lu.luxtrust.flowers.configuration;

import lu.luxtrust.flowers.properties.JWTProperties;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.security.RequestorRestAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@Configuration
@EnableSwagger2
@EnableConfigurationProperties(JWTProperties.class)
public class SwaggerConfiguration {

    @Autowired
    private JWTProperties jwtProperties;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(RestAuthenticationToken.class, RequestorRestAuthenticationToken.class, BindingResult.class)
                .securitySchemes(Arrays.asList(jwtHeaderSecuritySchema(), sslClientCertSecuritySchema()))
                .securityContexts(Arrays.asList(securityContextUIApi(), securityContextExternalApi()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("lu.luxtrust.flowers.controller"))
                .paths(PathSelectors.regex("/api.*"))
                .build();
    }

    private ApiKey sslClientCertSecuritySchema() {
        return new ApiKey("SSL_CLIENT_CERT", "SSL_CLIENT_CERT", "HEADER");
    }

    private ApiKey jwtHeaderSecuritySchema() {
        return new ApiKey("JWT_HEADER", jwtProperties.getAuthHeader(), "HEADER");
    }

    private SecurityContext securityContextUIApi() {
        return SecurityContext.builder()
                .securityReferences(Arrays.asList(new SecurityReference("JWT_HEADER", new AuthorizationScope[]{new AuthorizationScope("api", "Access to API")})))
                .forPaths(PathSelectors.regex("/api/(?!(external|static|health|login)).+"))
                .build();
    }

    private SecurityContext securityContextExternalApi() {
        return SecurityContext.builder()
                .securityReferences(Arrays.asList(new SecurityReference("SSL_CLIENT_CERT", new AuthorizationScope[]{new AuthorizationScope("api/extrenal", "Access to external API")})))
                .forPaths(PathSelectors.regex("/api/external/(.+)"))
                .build();
    }

    @Bean
    public SecurityConfiguration securityConfiguration() {
        return new SecurityConfiguration(null,null,null, null, null, ApiKeyVehicle.HEADER, jwtProperties.getAuthHeader(), " ");
    }
}
