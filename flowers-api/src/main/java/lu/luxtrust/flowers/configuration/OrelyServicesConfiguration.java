package lu.luxtrust.flowers.configuration;

import lu.luxtrust.flowers.properties.OrelyAuthProperties;
import lu.luxtrust.flowers.properties.OrelyKeystoreProperties;
import lu.luxtrust.flowers.properties.OrelyProperties;
import lu.luxtrust.flowers.security.orely.OrelyTrustedCertificate;
import lu.luxtrust.orely.api.factory.SAMLCredentialFactory;
import lu.luxtrust.orely.api.saml.SAMLCredential;
import lu.luxtrust.orely.api.service.RequestService;
import lu.luxtrust.orely.api.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.security.KeyStore;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties({OrelyKeystoreProperties.class, OrelyAuthProperties.class, OrelyProperties.class})
public class OrelyServicesConfiguration {

    private final OrelyKeystoreProperties orelyKeystoreProps;
    private final OrelyAuthProperties orelyAuthProps;
    private final OrelyProperties orelyProps;
    private final KeyStore keyStore;

    @Autowired
    public OrelyServicesConfiguration(OrelyKeystoreProperties orelyKeystoreProps,
                                      OrelyAuthProperties orelyAuthProps,
                                      @Qualifier("orelyKeyStore") KeyStore keyStore,
                                      OrelyProperties orelyProps) {
        this.orelyKeystoreProps = orelyKeystoreProps;
        this.orelyAuthProps = orelyAuthProps;
        this.orelyProps = orelyProps;
        this.keyStore = keyStore;
    }

    @Bean
    public Set<OrelyTrustedCertificate> orelyTrustedCertificates() {
        return orelyProps.getTrustedCertificates().stream().map(this::toOrelyAuthorizedCertificate).collect(Collectors.toSet());
    }

    private OrelyTrustedCertificate toOrelyAuthorizedCertificate(OrelyProperties.TrustedCertificateProperties props) {
        return new OrelyTrustedCertificate(props.getIssuer(), new BigInteger(props.getCertificateSerialNumber(), 16));
    }

    @Bean
    public SAMLCredential samlCredential() throws Exception {
        return SAMLCredentialFactory.createSAMLCredential(keyStore, orelyKeystoreProps.getCertificateAlias(), orelyKeystoreProps.getCertificatePassword());
    }

    @Bean
    public RequestService orelyRequestService() throws Exception {
        return new RequestService(samlCredential(),
                orelyAuthProps.getDestinationUrl(),
                orelyAuthProps.getReturnUrl(),
                orelyAuthProps.getIssuerUrl(),
                orelyAuthProps.getIdPrefix(),
                orelyAuthProps.isEnableOcsp());
    }

    @Bean
    public ResponseService orelyResponseService() throws Exception {
        return new ResponseService(samlCredential(), orelyAuthProps.isEnableOcsp());
    }

    @PostConstruct
    public void setSystemProperties() {
        System.setProperty("lu.luxtrust.certificate.validator.config.path", orelyProps.getCertificateValidatorPath());
    }
}
