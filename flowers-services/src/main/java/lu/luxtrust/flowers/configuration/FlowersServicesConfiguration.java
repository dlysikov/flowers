package lu.luxtrust.flowers.configuration;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.integration.ltss.client.LTSSClient;
import lu.luxtrust.flowers.integration.ltss.client.impl.LTSSClientImpl;
import lu.luxtrust.flowers.integration.ltss.client.impl.LTSSClientMockImpl;
import lu.luxtrust.flowers.properties.NotificationProperties;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

@Configuration
@EnableConfigurationProperties(NotificationProperties.class)
public class FlowersServicesConfiguration {

    private static final String ESEAL_LTSS_URL = "eseal.LTSS_URL";

    @Value("${" + ESEAL_LTSS_URL + "}")
    private String lttsServiceURL;

    @Bean
    public XMLInputFactory xmlInputFactory() {
        return XMLInputFactory.newInstance();
    }

    @Bean
    public JAXBContext jaxbContext() throws Exception {
        return JAXBContext.newInstance(CertificateOrder.class);
    }

    @Bean
    public Unmarshaller unmarshaller() throws Exception {
        return jaxbContext().createUnmarshaller();
    }

    @Bean
    public SchemaFactory schemaFactory() {
        return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    @Bean
    public Validator ordersValidator() throws Exception {
        Schema schema = schemaFactory().newSchema(getClass().getResource("/xsd/order.xsd"));
        return schema.newValidator();
    }

    @Bean
    public RestTemplate restTemplateFactory() throws Exception {
        SSLContextBuilder sslcontext = new SSLContextBuilder();
        sslcontext.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        CloseableHttpClient build = HttpClients.custom().setSSLContext(sslcontext.build()).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(build);

        return new RestTemplateBuilder()
                .messageConverters(new MappingJackson2HttpMessageConverter(), new FormHttpMessageConverter())
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    public Tika tika() {
        return new Tika();
    }

    @Bean
    @ConditionalOnProperty(matchIfMissing = true, name = "ltss.env", havingValue = "prod")
    public LTSSClient ltssClient() {
        return new LTSSClientImpl();
    }

    @Bean
    @ConditionalOnProperty(name = "ltss.env", havingValue = "mock")
    public LTSSClient ltssClientMock() {
        return new LTSSClientMockImpl();
    }
}
