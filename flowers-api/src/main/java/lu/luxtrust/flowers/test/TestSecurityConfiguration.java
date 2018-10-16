package lu.luxtrust.flowers.test;

import lu.luxtrust.flowers.configuration.WebSecurityConfiguration;
import lu.luxtrust.flowers.properties.CorsProperties;
import lu.luxtrust.flowers.properties.OrelyAuthProperties;
import lu.luxtrust.flowers.repository.RequestorRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.security.jwt.JWTService;
import lu.luxtrust.flowers.security.orely.OrelyAuthenticationFilter;
import lu.luxtrust.flowers.security.orely.OrelyAuthenticationService;
import lu.luxtrust.flowers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(CorsProperties.class)
@Profile({"local", "develop", "qa", "test"})
public class TestSecurityConfiguration extends WebSecurityConfiguration {
    @Autowired
    public TestSecurityConfiguration(OrelyAuthenticationService orelyAuthenticationService,
                                 UserService userService,
                                 OrelyAuthProperties orelyAuthProperties,
                                 RequestorRepository RequestorRepository,
                                 CorsProperties corsProperties,
                                 JWTService jwtService,
                                 @Value("${external.api.ssl_client_cert_header:SSL_CLIENT_CERT}") String externalApiSSLClientCertHeader) {
        super(orelyAuthenticationService,userService,orelyAuthProperties,RequestorRepository,corsProperties,jwtService, externalApiSSLClientCertHeader);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       super.configure(http);
       http.authorizeRequests().antMatchers("/api/login/mock").permitAll();
       http.addFilterBefore(testAuthFilter(), OrelyAuthenticationFilter.class);
    }

    private OrelyTestAuthenticationFilter testAuthFilter() {
        OrelyTestAuthenticationFilter filter = new OrelyTestAuthenticationFilter("/api/login/mock", userService);
        filter.setAuthenticationSuccessHandler((HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            jwtService.addTokenToResponse(response, (RestAuthenticationToken) authentication);
            jwtService.addRefreshTokenToResponse(response, (RestAuthenticationToken) authentication);
        });
        filter.setAuthenticationFailureHandler((HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        });
        return filter;
    }
}
