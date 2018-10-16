package lu.luxtrust.flowers.configuration;

import lu.luxtrust.flowers.properties.KeyStoreProperties;
import lu.luxtrust.flowers.tools.KeyStoreWithConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyStore;

@Configuration
@ConditionalOnExpression("!'${keystore.orely.type}'.equals('NONE')")
public class KeyStoreConfiguration {

    @Bean
    public FlowersKeyStoreFactory keyStoreFactory() {
        return new FlowersKeyStoreFactory();
    }

    @Bean
    @ConfigurationProperties(prefix = "keystore.orely")
    public KeyStoreProperties orelyKeyStoreProperties() {
        return new KeyStoreProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "keystore.lrs")
    public KeyStoreProperties lrsKeyStoreProperties() {
        return new KeyStoreProperties();
    }

    @Bean
    public KeyStore lrsKeyStore() throws Exception {
        KeyStoreProperties props = lrsKeyStoreProperties();
        return keyStoreFactory().createKeyStore(props, props.getType());
    }

    @Bean
    public KeyStore orelyKeyStore() throws Exception {
        KeyStoreProperties props = orelyKeyStoreProperties();
        return keyStoreFactory().createKeyStore(props, props.getType());
    }

    @Bean
    public KeyStoreWithConfig lrsKeyStoreWithConfig() throws Exception {
        return new KeyStoreWithConfig(lrsKeyStore(), lrsKeyStoreProperties());
    }

    @Bean
    public KeyStoreWithConfig orelyKeyStoreWithConfig() throws Exception {
        return new KeyStoreWithConfig(orelyKeyStore(), orelyKeyStoreProperties());
    }
}
