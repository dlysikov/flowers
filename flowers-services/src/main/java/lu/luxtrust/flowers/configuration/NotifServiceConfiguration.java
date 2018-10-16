package lu.luxtrust.flowers.configuration;

import lu.luxtrust.flowers.properties.NotifProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({NotifProperties.class})
public class NotifServiceConfiguration {
}
