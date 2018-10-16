package lu.luxtrust.flowers.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lu.luxtrust.flowers.properties.JWTProperties;
import lu.luxtrust.flowers.security.JsonSanitizingDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletContext;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties({JWTProperties.class})
public class FlowerConfiguration extends WebMvcConfigurerAdapter {

    private Integer cachePeriod;
    private Long fileUploadMaxSize;
    private ServletContext servletContext;

    @Autowired
    public FlowerConfiguration(@Value("${http.static.cache.period}") Integer cachePeriod, ServletContext servletContext) {
        this.cachePeriod = cachePeriod;
        this.fileUploadMaxSize = fileUploadMaxSize;
        this.servletContext = servletContext;
    }

    @Bean
    public String applicationVersion() throws Exception{
        try {
            Properties props = new Properties();
            props.load(servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"));
            return props.getProperty("Implementation-Version");
        } catch (Exception e) {
            return "LOCAL";
        }
    }

    @Bean
    public HttpMessageConverter<?> jsonConverter() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new JsonSanitizingDeserializer());
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.registerModule(module);
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/")
                .setCachePeriod(this.cachePeriod);

        registry.addResourceHandler("/documentation/**")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/xsd/**").addResourceLocations("classpath:/xsd/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/documentation/v2/api-docs", "/v2/api-docs").setKeepQueryParams(true);
        registry.addRedirectViewController("/documentation/swagger-resources/configuration/ui","/swagger-resources/configuration/ui");
        registry.addRedirectViewController("/documentation/swagger-resources/configuration/security","/swagger-resources/configuration/security");
        registry.addRedirectViewController("/documentation/swagger-resources", "/swagger-resources");
    }
}
