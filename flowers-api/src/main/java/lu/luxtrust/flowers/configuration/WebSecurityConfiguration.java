package lu.luxtrust.flowers.configuration;

import lu.luxtrust.flowers.properties.CorsProperties;
import lu.luxtrust.flowers.properties.OrelyAuthProperties;
import lu.luxtrust.flowers.repository.RequestorRepository;
import lu.luxtrust.flowers.security.RestAuthenticationEntryPoint;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.security.certificate.service.X509CertificateAuthenticatedAuthenticationProvider;
import lu.luxtrust.flowers.security.certificate.service.X509CertificateAuthenticatedProcessingFilter;
import lu.luxtrust.flowers.security.jwt.JWTPreAuthenticatedAuthenticationProvider;
import lu.luxtrust.flowers.security.jwt.JWTPreAuthenticatedProcessingFilter;
import lu.luxtrust.flowers.security.jwt.JWTRefreshTokenAuthenticationFilter;
import lu.luxtrust.flowers.security.jwt.JWTService;
import lu.luxtrust.flowers.security.orely.OrelyAuthenticationFilter;
import lu.luxtrust.flowers.security.orely.OrelyAuthenticationService;
import lu.luxtrust.flowers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.cert.CertificateFactory;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(CorsProperties.class)
@Profile("release")
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final OrelyAuthenticationService orelyAuthenticationService;
    private final OrelyAuthProperties orelyAuthProperties;
    private final RequestorRepository RequestorRepository;
    private final CorsProperties corsProperties;
    private final String externalApiSSLClientCertHeader;

    protected final UserService userService;
    protected final JWTService jwtService;

    @Autowired
    public WebSecurityConfiguration(OrelyAuthenticationService orelyAuthenticationService,
                                    UserService userService,
                                    OrelyAuthProperties orelyAuthProperties,
                                    RequestorRepository RequestorRepository,
                                    CorsProperties corsProperties,
                                    JWTService jwtService,
                                    @Value("${external.api.ssl_client_cert_header}") String externalApiSSLClientCertHeader) {
        this.orelyAuthenticationService = orelyAuthenticationService;
        this.userService = userService;
        this.orelyAuthProperties = orelyAuthProperties;
        this.jwtService = jwtService;
        this.corsProperties = corsProperties;
        this.RequestorRepository = RequestorRepository;
        this.externalApiSSLClientCertHeader = externalApiSSLClientCertHeader;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new JWTPreAuthenticatedAuthenticationProvider(userService))
            .authenticationProvider(new X509CertificateAuthenticatedAuthenticationProvider(RequestorRepository));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
             .authorizeRequests()
                .antMatchers("/api/login", "/api/login/reload", "/", "/api/login/refresh","/api/login/saml-response", "/api/static/**").permitAll()
                .antMatchers("/api/orders/user/validate/**").permitAll()
                .antMatchers("/api/health").permitAll()
                .antMatchers("/api/**", "/api/external/**").authenticated()
             .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
             .and()
                .headers().frameOptions().disable()
             .and()
                .csrf().disable()
                .cors()
             .and()
                .addFilterBefore(refreshTokenFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterAt(preAuthenticatedFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterAfter(RequestorPreAuthenticatedFilter(), JWTPreAuthenticatedProcessingFilter.class)
                .addFilterAfter(orelyAuthenticationFilter(), X509CertificateAuthenticatedProcessingFilter.class)
                .exceptionHandling().authenticationEntryPoint(new RestAuthenticationEntryPoint());
    }

    private X509CertificateAuthenticatedProcessingFilter RequestorPreAuthenticatedFilter() throws Exception {
        String regexPattern = "/api/external/(.+)";
        RequestMatcher matcher = new OrRequestMatcher(Stream.of(HttpMethod.values()).map((hm) -> new RegexRequestMatcher(regexPattern, hm.name(), Boolean.TRUE)).collect(Collectors.toList()));
        X509CertificateAuthenticatedProcessingFilter filter = new X509CertificateAuthenticatedProcessingFilter(externalApiSSLClientCertHeader, x509CertificateFactory(), matcher);
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    private JWTPreAuthenticatedProcessingFilter preAuthenticatedFilter() throws Exception {
        String regexPattern = "/api/(?!(external)).+";
        RequestMatcher matcher = new OrRequestMatcher(Stream.of(HttpMethod.values()).map((hm) -> new RegexRequestMatcher(regexPattern, hm.name(), Boolean.TRUE)).collect(Collectors.toList()));
        JWTPreAuthenticatedProcessingFilter jwtPreAuthenticatedProcessingFilter = new JWTPreAuthenticatedProcessingFilter(jwtService, matcher);
        jwtPreAuthenticatedProcessingFilter.setAuthenticationManager(authenticationManager());
        return jwtPreAuthenticatedProcessingFilter;
    }

    private JWTRefreshTokenAuthenticationFilter refreshTokenFilter() {
        JWTRefreshTokenAuthenticationFilter jwtRefreshTokenFilter = new JWTRefreshTokenAuthenticationFilter("/api/login/refresh", jwtService, userService);
        jwtRefreshTokenFilter.setAuthenticationFailureHandler((HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        });
        jwtRefreshTokenFilter.setAuthenticationSuccessHandler((HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            jwtService.addTokenToResponse(response, (RestAuthenticationToken) authentication);
            jwtService.addRefreshTokenToResponse(response, (RestAuthenticationToken) authentication);
        });
        jwtRefreshTokenFilter.setContinueChainBeforeSuccessfulAuthentication(Boolean.FALSE);
        return jwtRefreshTokenFilter;
    }

    private OrelyAuthenticationFilter orelyAuthenticationFilter() {
        OrelyAuthenticationFilter orelyAuthenticationFilter = new OrelyAuthenticationFilter(orelyAuthProperties.getLoginProcessingUrl(), orelyAuthenticationService, orelyAuthProperties, userService);
        orelyAuthenticationFilter.setAuthenticationSuccessHandler((HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            jwtService.addTokenToResponse(response, (RestAuthenticationToken) authentication);
            jwtService.addRefreshTokenToResponse(response, (RestAuthenticationToken) authentication);
            request.getRequestDispatcher("/api/login/success").forward(request, response);
        });
        orelyAuthenticationFilter.setAuthenticationFailureHandler((HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) -> {
            request.getRequestDispatcher("/api/login/reload").forward(request, response);
        });
        return orelyAuthenticationFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        configuration.setAllowCredentials(corsProperties.getAllowCredentials());
        configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        configuration.setExposedHeaders(corsProperties.getExposeHeaders());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsProperties.getMapping(), configuration);
        return source;
    }

    @Bean
    public CertificateFactory x509CertificateFactory() throws Exception  {
        return CertificateFactory.getInstance("X509");
    }
}
