package lu.luxtrust.flowers.configuration;

import lu.luxtrust.flowers.repository.*;
import lu.luxtrust.flowers.security.method.FlowersMethodSecurityExpressionHandler;
import lu.luxtrust.flowers.security.method.MethodSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Autowired
    private MethodSecurityContext methodSecurityContext;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new FlowersMethodSecurityExpressionHandler(methodSecurityContext, trustResolver());
    }

    @Bean
    public AuthenticationTrustResolver trustResolver() {
        return new AuthenticationTrustResolverImpl();
    }
}
