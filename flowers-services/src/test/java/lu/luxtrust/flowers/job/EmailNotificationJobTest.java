package lu.luxtrust.flowers.job;

import lu.luxtrust.flowers.AbstractWithSpringContextTest;
import lu.luxtrust.flowers.entity.builder.*;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.OrderUserValidatePage;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.rule.SmtpServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailNotificationJobTest  extends AbstractWithSpringContextTest {

    public static final String NOT_EXPIRED_PAGE_HASH = "aaa3";
    @Autowired
    private EmailNotificationJob emailNotificationJob;
    @Autowired
    private TestEntityManager entityManager;

    @Rule
    public SmtpServerRule smtpServerRule = new SmtpServerRule();

    private CertificateOrder orderToRegeneratePage;
    private CertificateOrder orderForNotExpiredPage;
    private OrderUserValidatePage pageToBeRegenerated;
    private OrderUserValidatePage notExpiredPage;

    @Before
    public void init() {
        Requestor requestor = RequestorBuilder.newBuilder().build();
        requestor = entityManager.persist(requestor);
        Unit unit = UnitBuilder.newBuilder().requestor(requestor).build();
        entityManager.persist(unit.getCountry());
        unit = entityManager.persist(unit);

        User issuer = UserBuilder.newBuilder().build();
        entityManager.persist(issuer);

        orderToRegeneratePage = CertificateOrderBuilder.newBuilder().unit(unit).issuer(issuer).status(OrderStatus.USER_DRAFT).build();
        CertificateOrder secondOrder = CertificateOrderBuilder.newBuilder().issuer(issuer).unit(unit).status(OrderStatus.DRAFT).build();
        CertificateOrder thirdOrder = CertificateOrderBuilder.newBuilder().issuer(issuer).unit(unit).status(OrderStatus.SENT_TO_LRS).build();
        orderForNotExpiredPage = CertificateOrderBuilder.newBuilder().issuer(issuer).unit(unit).status(OrderStatus.USER_DRAFT).build();

        entityManager.persist(orderToRegeneratePage);
        entityManager.persist(secondOrder);
        entityManager.persist(thirdOrder);
        entityManager.persist(orderForNotExpiredPage);

        pageToBeRegenerated = OrderUserValidatePageBuilder.newBuilder().order(orderToRegeneratePage).expirationTime(new Date(new Date().getTime() - 1000000)).pageHash("aaa").build();
        OrderUserValidatePage secondPage = OrderUserValidatePageBuilder.newBuilder().order(secondOrder).expirationTime(new Date(new Date().getTime() - 1000000)).pageHash("aaa1").build();
        OrderUserValidatePage thirdPage = OrderUserValidatePageBuilder.newBuilder().order(thirdOrder).expirationTime(new Date(new Date().getTime() - 1000000)).pageHash("aaa2").build();
        notExpiredPage = OrderUserValidatePageBuilder.newBuilder().order(orderForNotExpiredPage).expirationTime(new Date(new Date().getTime() + 1000000)).pageHash(NOT_EXPIRED_PAGE_HASH).build();
        entityManager.persist(pageToBeRegenerated);
        entityManager.persist(secondPage);
        entityManager.persist(thirdPage);
        entityManager.persist(notExpiredPage);
    }

    @Test
    public void sendOrderValidationEmailToUser() throws IOException, MessagingException {
        emailNotificationJob.sendOrderValidationEmailToUser();

        List<OrderUserValidatePage> pages = entityManager.getEntityManager().createQuery("select p from OrderUserValidatePage p order by p.order.id").getResultList();
        assertThat(pages).hasSize(2);

        assertThat(pages.get(0).getOrder().getId()).isEqualTo(orderToRegeneratePage.getId());
        assertThat(pages.get(0).getPageHash()).isNotEqualTo(pageToBeRegenerated.getPageHash());

        assertThat(pages.get(1).getOrder().getId()).isEqualTo(orderForNotExpiredPage.getId());
        assertThat(pages.get(1).getPageHash()).isEqualTo(notExpiredPage.getPageHash());

        MimeMessage[] messages = smtpServerRule.getMessages();
        assertThat(messages).hasSize(1);
        assertThat(String.valueOf(messages[0].getContent())).contains(pages.get(0).getPageHash());
    }
}