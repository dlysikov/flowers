package lu.luxtrust.flowers.configuration;

import com.safenetinc.luna.provider.LunaCertificateX509;
import com.safenetinc.luna.provider.key.LunaKey;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.integration.lrs.mock.LrsWsMock;
import lu.luxtrust.flowers.integration.lrs.ws.LrsWS;
import lu.luxtrust.flowers.integration.lrs.ws.LrsWSRegistrationAuthority;
import lu.luxtrust.flowers.integration.lrs.ws.LrsWSService;
import lu.luxtrust.flowers.job.LrsStatusJob;
import lu.luxtrust.flowers.properties.LrsProperties;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.repository.ESealOrderRepository;
import lu.luxtrust.flowers.service.*;
import lu.luxtrust.flowers.service.util.SignatureTextGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Calendar;

@Configuration
@EnableConfigurationProperties({LrsProperties.class})
public class LrsConfiguration {

    private static final long HSM_CERT_SERIAL_NUMBER = 0L;

    private final LrsProperties properties;
    private final KeyStore keyStore;
    private final String hsmCertSubject;

    @Autowired
    public LrsConfiguration(LrsProperties properties, @Qualifier("lrsKeyStore") KeyStore keyStore) {
        this.properties = properties;
        this.keyStore = keyStore;
        this.hsmCertSubject =
                "T=LuxTrust RA Officer,SERIALNUMBER=" + properties.getRaSerial() +
                ",GIVENNAME=" + properties.getRaName() +
                ",SURNAME=" + properties.getRaSurname() +
                ",CN=" + properties.getRaName() + " " + properties.getRaSurname() +
                ",OU=" + properties.getRaOperatorId() +
                ",OU=" + properties.getRaNetworkId() +
                ",O=LuxTrust S.A.,L=LU,C=LU";
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Bean
    @ConditionalOnProperty(matchIfMissing = true, name = "lrs.env", havingValue = "prod")
    public LrsWS getLrsWS() throws MalformedURLException {
        QName qName = new QName("http://ws.lrs.luxtrust.lu/", "LrsWSService");
        return new LrsWSService(new URL(properties.getLrsWsUrl()), qName).getLrsWSPort();
    }

    @Bean
    @ConditionalOnProperty(name = "lrs.env", havingValue = "mock")
    public LrsWS getLrsWSmock() {
        return new LrsWsMock();
    }

    @Bean
    public LrsWSRegistrationAuthority getLrsWSRegistrationAuthority() {
        LrsWSRegistrationAuthority wsRA = new LrsWSRegistrationAuthority();
        wsRA.setNetworkID(properties.getRaNetworkId());
        wsRA.setOperatorID(properties.getRaOperatorId());
        return wsRA;
    }

    @Bean
    public Key getLrsKey() throws Exception {
        String pass = properties.getKeyPassword() == null ? "" : properties.getKeyPassword();
        return keyStore.getKey(properties.getKeyName(), pass.toCharArray());
    }

    @Bean
    public X509Certificate getLrsCertificate() throws Exception {
        try {
            PublicKey publicKey = (PublicKey) LunaKey.LocateKeyByAlias(this.properties.getPublicKeyName());
            PrivateKey privateKey = (PrivateKey) getLrsKey();

            Calendar validFrom = Calendar.getInstance();
            validFrom.add(Calendar.DAY_OF_YEAR, -1);
            Calendar validTo = Calendar.getInstance();
            validTo.add(Calendar.YEAR, 2);

            return LunaCertificateX509.SelfSign(new KeyPair(publicKey, privateKey), hsmCertSubject, BigInteger.valueOf(HSM_CERT_SERIAL_NUMBER), validFrom.getTime(), validTo.getTime());
        } catch (Throwable t) {
            try {
                String pass = properties.getKeyPassword() == null ? "" : properties.getKeyPassword();
                KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(properties.getKeyName(), new KeyStore.PasswordProtection(pass.toCharArray()));
                return (X509Certificate) entry.getCertificate();
            } catch (Exception e) {
                throw t;
            }
        }
    }

    @Bean
    public LrsStatusJob<CertificateOrder> certificateOrderJob(CertificateOrderRepository repository, LrsService lrsService, CertificateOrderService service) {
        return new LrsStatusJob<CertificateOrder>(repository, lrsService, service) {

            @Override
            protected LrsStatusHandler getLrsStatusHandler(OrderService orderService, String orderNumber, OrderStatus status) {
                return new LrsStatusHandler.Certificate(orderService, orderNumber, status);
            }
        };
    }

    @Bean
    public LrsStatusJob<ESealOrder> eSealOrderJob(ESealOrderRepository repository, LrsService lrsService, ESealService service) {
        return new LrsStatusJob<ESealOrder>(repository, lrsService, service) {
            @Override
            protected LrsStatusHandler getLrsStatusHandler(OrderService orderService, String orderNumber, OrderStatus status) {
                return new LrsStatusHandler.ESeal(orderService, orderNumber, status);
            }
        };
    }

    @Bean
    public SignatureTextGenerator getSignatureTextGenerator() {
        return new SignatureTextGenerator();
    }

}
