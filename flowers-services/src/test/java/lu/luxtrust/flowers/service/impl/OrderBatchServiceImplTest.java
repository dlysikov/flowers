package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.AbstractWithSpringContextTest;
import lu.luxtrust.flowers.entity.builder.*;
import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.common.Nationality;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.CompanyIdentifier;
import lu.luxtrust.flowers.entity.enrollment.Holder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.model.ImportResult;
import lu.luxtrust.flowers.service.OrderBatchService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderBatchServiceImplTest  extends AbstractWithSpringContextTest {

    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Smith";
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private OrderBatchService orderBatchService;
    private User issuer;
    private Requestor requestor;
    private CertificateOrder duplicate;

    @Before
    public void init() {
        Country country = CountryBuilder.newBuilder().countryCode("ua").id(null).build();
        Nationality nationality = NationalityBuilder.newBuilder().nationalityCode("ua").id(null).build();
        entityManager.persist(country);
        entityManager.persist(nationality);

        Holder holder = HolderBuilder.newBuilder()
                .firstName(FIRST_NAME)
                .surName(LAST_NAME)
                .notifyEmail("aaa@gmail.com")
                .nationality(nationality)
                .build();

        issuer = UserBuilder.newBuilder().build();
        duplicate = CertificateOrderBuilder.newBuilder().unit(null).holder(holder).issuer(issuer).build();
        requestor = RequestorBuilder.newBuilder().name("GGG").build();

        entityManager.persist(issuer);
        entityManager.persist(duplicate);
        entityManager.persist(requestor);

        Unit unit = UnitBuilder.newBuilder()
                .identifierType(CompanyIdentifier.Type.OTHER)
                .identifierValue("1111111111")
                .country(country)
                .requestor(requestor)
                .id(null)
                .build();
        entityManager.persist(unit);
    }

    @Test
    public void processOrdersXML() {
        ImportResult<CertificateOrder> result = orderBatchService.processOrdersXML(getClass().getResourceAsStream("/orders.xml"), issuer.getId());
        assertThat(result.getSuccessful()).isEqualTo(4);
        assertThat(result.getFailed()).isEqualTo(1);

        List<CertificateOrder> failedDetails = result.getFailedDetails();
        assertThat(failedDetails).hasSize(1);

        CertificateOrder duplicate = failedDetails.get(0);
        assertThat(duplicate.getId()).isEqualTo(duplicate.getId());
    }

}