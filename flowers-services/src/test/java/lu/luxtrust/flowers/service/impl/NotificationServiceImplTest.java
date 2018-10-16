package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.AbstractWithSpringContextTest;
import lu.luxtrust.flowers.entity.builder.*;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.OrderUserValidatePage;
import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.properties.NotificationProperties;
import lu.luxtrust.flowers.rule.SmtpServerRule;
import lu.luxtrust.flowers.service.NotificationService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationServiceImplTest  extends AbstractWithSpringContextTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private NotificationProperties notificationProperties;

    @Rule
    public SmtpServerRule smtpServerRule = new SmtpServerRule();

    private Set<Role> roles = new HashSet<>();
    private User issuer;

    @Before
    public void init(){
        Role role = new Role();
        role.setRoleType(RoleType.FLOWERS_ADMIN);
        entityManager.persist(role);
        roles.add(role);
        issuer = UserBuilder.newBuilder().roles(roles).build();
        entityManager.persist(issuer);
    }

    @Test
    public void notifyEndUserToValidateOrder() throws MessagingException, IOException {
        CertificateOrder order = CertificateOrderBuilder.newBuilder().issuer(issuer).unit(null).build();
        order.getHolder().setNotifyEmail("ivan.skrypka-ext@luxtrust.lu");
        entityManager.persist(order);
        OrderUserValidatePage page = OrderUserValidatePageBuilder.newBuilder().order(order).build();
        entityManager.persist(page);

        notificationService.notifyEndUserToValidateOrder(page);

        MimeMessage[] messages = smtpServerRule.getMessages();
        assertThat(messages).hasSize(1);

        MimeMessage message = messages[0];
        assertThat(message.getSubject()).isEqualTo(notificationProperties.getEndUser().getDefaultSubject());
        assertThat(String.valueOf(message.getContent())).contains(page.getPageHash());
    }

    @Test
    public void notifyEndUsersToValidateOrder() throws MessagingException, IOException {
        CertificateOrder order = CertificateOrderBuilder.newBuilder().unit(null).issuer(issuer).build();
        order.getHolder().setNotifyEmail("ivan.skrypka-ext@luxtrust.lu");
        entityManager.persist(order);
        OrderUserValidatePage page = OrderUserValidatePageBuilder.newBuilder().order(order).build();
        entityManager.persist(page);

        notificationService.notifyEndUserToValidateOrder(Collections.singletonList(page));

        MimeMessage[] messages = smtpServerRule.getMessages();
        assertThat(messages).hasSize(1);

        MimeMessage message = messages[0];
        assertThat(message.getSubject()).isEqualTo(notificationProperties.getEndUser().getDefaultSubject());
        assertThat(String.valueOf(message.getContent())).contains(page.getPageHash());
    }

    @Test
    public void notifyOrderIssuerToSignTheOrder() throws MessagingException, IOException {
        CertificateOrder order = CertificateOrderBuilder.newBuilder().unit(null).remoteId(Boolean.TRUE).status(OrderStatus.CSD_REVIEW_REQUIRED).issuer(issuer).build();
        entityManager.persist(order);

        notificationService.notify(Collections.singletonMap("order", order), Collections.singletonList("ivan.skrypka-ext@luxtrust.lu"), notificationProperties.getIssuerCsdRequired());

        MimeMessage[] messages = smtpServerRule.getMessages();
        assertThat(messages).hasSize(1);

        MimeMessage message = messages[0];
        assertThat(message.getSubject()).isEqualTo(notificationProperties.getIssuerCsdRequired().getDefaultSubject());
        assertThat(String.valueOf(message.getContent())).contains(order.getId().toString());
        assertThat(String.valueOf(message.getContent())).doesNotContain("registration process");
        assertThat(String.valueOf(message.getContent())).doesNotContain("remote identification");
    }

    @Test
    public void notifyEndUserToActivateCertificate() throws MessagingException, IOException {
        CertificateOrder order = CertificateOrderBuilder.newBuilder().issuer(issuer).build();
        order.getHolder().setNotifyEmail("ivan.skrypka-ext@luxtrust.lu");
        entityManager.persist(order);

        notificationService.notifyEndUserToActivateCertificate(order);

        MimeMessage[] messages = smtpServerRule.getMessages();
        assertThat(messages).hasSize(1);

        MimeMessage message = messages[0];
        assertThat(message.getSubject()).isEqualTo(notificationProperties.getEndUserInviteToActivate().getDefaultSubject());

        String messageString = String.valueOf(message.getContent());
        assertThat(messageString).contains("LuxTrust Token");
        assertThat(messageString).contains("LuxTrust Mobile");
    }
}