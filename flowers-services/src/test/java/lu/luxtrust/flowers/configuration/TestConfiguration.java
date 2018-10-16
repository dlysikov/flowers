package lu.luxtrust.flowers.configuration;

import lu.luxtrust.flowers.integration.lrs.ws.LrsWS;
import org.hibernate.validator.HibernateValidator;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.security.KeyStore;

@Configuration
public class TestConfiguration {

    @Bean
    public LrsWS getLrsWS() {
        return Mockito.mock(LrsWS.class);
    }

    @Bean
    public Validator validator (final AutowireCapableBeanFactory autowireCapableBeanFactory) {
        ValidatorFactory validatorFactory = Validation.byProvider( HibernateValidator.class )
                .configure().constraintValidatorFactory(new SpringConstraintValidatorFactory(autowireCapableBeanFactory))
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    @Bean(name = "lrsKeyStore")
    public KeyStore lrsKeyStore() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(this.getClass().getResourceAsStream("/flowers-keystore.jks"), "LuxTrust".toCharArray());
        return ks;
    }
}
