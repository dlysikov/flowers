package lu.luxtrust.flowers;

import lu.luxtrust.flowers.configuration.LrsConfiguration;
import lu.luxtrust.flowers.rule.SmtpServerRule;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.thymeleaf.util.StringUtils;

import java.net.ServerSocket;

@EntityScan(basePackages = "lu.luxtrust.flowers.entity")
@SpringBootApplication
@ComponentScan(basePackages = "lu.luxtrust.flowers")
@ImportAutoConfiguration({MailSenderAutoConfiguration.class,ThymeleafAutoConfiguration.class, ValidationAutoConfiguration.class})
public class TestApplication {
    static {
        SmtpServerRule.initSmtpPort();
    }
}
