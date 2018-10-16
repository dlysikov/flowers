package lu.luxtrust.flowers.service.impl;

import com.google.common.collect.Lists;
import lu.luxtrust.flowers.AbstractWithSpringContextTest;
import lu.luxtrust.flowers.entity.builder.*;
import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.CompanyIdentifier;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.model.FieldFilter;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.service.UnitService;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class UnitServiceImplTest extends AbstractWithSpringContextTest {

    @Autowired
    private UnitService unitService;
    @Autowired
    private TestEntityManager entityManager;

    private Country country;
    private CertificateOrder order;
    private User user;
    private Requestor si;

    @Before
    public void init() {
        si = entityManager.persist(RequestorBuilder.newBuilder().build());
        user = entityManager.persist(UserBuilder.newBuilder().build());
        order = entityManager.persist(CertificateOrderBuilder.newBuilder().unit(null).build());
        country = entityManager.persist(CountryBuilder.newBuilder().build());
    }

    @Test
    public void findAllWithPageParams() {
        Unit unitOne = entityManager.persist(UnitBuilder.newBuilder().commonName("fest").requestor(si).country(country).build());
        Unit unitTwo = entityManager.persist(UnitBuilder.newBuilder().commonName("test").requestor(si).country(country).build());

        PageParams pp = new PageParams();
        pp.setPageNumber(1);
        pp.setPageSize(1);

        List<Unit> units = unitService.findAll(pp);
        assertThat(units.size()).isEqualTo(1);
        assertThat(units.get(0).getId()).isEqualTo(unitTwo.getId());

        pp.setPageSize(10);
        pp.setFilter(Arrays.asList(new FieldFilter("commonName", Lists.newArrayList("fes"), FieldFilter.ValueType.STRING_LIKE)));

        units = unitService.findAll(pp);
        assertThat(units.size()).isEqualTo(1);
        assertThat(units.get(0).getId()).isEqualTo(unitOne.getId());
    }

    @Test
    public void findAll() {
        Unit unitWithRequestor = entityManager.persist(UnitBuilder.newBuilder().requestor(si).country(country).build());
        Unit unitWithoutRequestor = entityManager.persist(UnitBuilder.newBuilder().requestor(null).country(country).build());

        List<Unit> units = unitService.findAll(new PageParams());
        assertThat(units.size()).isEqualTo(2);
        assertThat(units.stream().map(Unit::getId).collect(Collectors.toSet())).isEqualTo(Sets.newLinkedHashSet(unitWithoutRequestor.getId(), unitWithRequestor.getId()));
    }

    @Test
    public void isValuableDataModifiable() {
        Unit unitWithRequestor = entityManager.persist(UnitBuilder.newBuilder().requestor(si).country(country).build());
        assertThat(unitService.isValuableDataModifiable(unitWithRequestor.getId())).isTrue();
    }

    @Test
    public void isValuableDataModifiableOrderAssigned() {
        Unit unitWithRequestor = entityManager.persist(UnitBuilder.newBuilder().requestor(si).country(country).build());

        order.setUnit(unitWithRequestor);
        entityManager.persist(order);

        assertThat(unitService.isValuableDataModifiable(unitWithRequestor.getId())).isFalse();
    }

    @Test
    public void isValuableDataModifiableUserAssigned() {
        Unit unitWithRequestor = entityManager.persist(UnitBuilder.newBuilder().requestor(si).country(country).build());

        user.setUnit(unitWithRequestor);
        entityManager.persist(user);

        assertThat(unitService.isValuableDataModifiable(unitWithRequestor.getId())).isFalse();
    }

    @Test
    public void canBeDeleted() {
        Unit unitWithRequestor = entityManager.persist(UnitBuilder.newBuilder().requestor(si).country(country).build());
        assertThat(unitService.canBeDeleted(unitWithRequestor.getId())).isTrue();
    }

    @Test
    public void canBeDeletedOrderAssigned() {
        Unit unitWithRequestor = entityManager.persist(UnitBuilder.newBuilder().requestor(si).country(country).build());

        order.setUnit(unitWithRequestor);
        entityManager.persist(order);

        assertThat(unitService.canBeDeleted(unitWithRequestor.getId())).isFalse();
    }

    @Test
    public void canBeDeletedUserAssigned() {
        Unit unitWithRequestor = entityManager.persist(UnitBuilder.newBuilder().requestor(si).country(country).build());

        user.setUnit(unitWithRequestor);
        entityManager.persist(user);

        assertThat(unitService.canBeDeleted(unitWithRequestor.getId())).isFalse();
    }

    @Test
    public void changesCanBeAppliedOrderAssigned() {
        Unit unitWithRequestor = entityManager.persist(UnitBuilder.newBuilder().requestor(si).country(country).build());
        Unit unitCopy = UnitBuilder.newBuilder().requestor(si).country(country).id(unitWithRequestor.getId()).build();

        order.setUnit(unitWithRequestor);
        entityManager.persist(order);

        unitCopy.setCommonName("Test");

        assertThat(unitService.changesCanBeApplied(unitCopy)).isFalse();
    }


    @Test
    public void changesCanBeApplied() {
        Unit unitWithRequestor = entityManager.persist(UnitBuilder.newBuilder().requestor(si).country(country).build());
        Requestor siNew = entityManager.persist(RequestorBuilder.newBuilder().name("Some").build());
        Country countryNew = entityManager.persist(CountryBuilder.newBuilder().countryCode("EU").build());

        Unit unitCopy = UnitBuilder.newBuilder()
                .requestor(siNew)
                .country(countryNew)
                .identifierType(CompanyIdentifier.Type.VAT)
                .identifierValue("234234324234")
                .commonName("Test")
                .companyName("Test")
                .postCode("Test")
                .city("Test")
                .streetAndHouseNo("Test")
                .id(unitWithRequestor.getId()).build();

        assertThat(unitService.changesCanBeApplied(unitCopy)).isTrue();
    }

    @Test
    public void changesCanBeAppliedWhenOnlyOptional() {
        Unit unitWithRequestor = entityManager.persist(UnitBuilder.newBuilder().requestor(si).country(country).build());

        order.setUnit(unitWithRequestor);
        entityManager.persist(order);

        Unit unitCopy = UnitBuilder.newBuilder()
                .requestor(si)
                .id(unitWithRequestor.getId())
                .eMail("test.email@gmai.com")
                .phoneNumber("+12123432423")
                .streetAndHouseNo("new streeet")
                .city("new city")
                .postCode("new post code")
                .country(country)
                .build();

        assertThat(unitService.changesCanBeApplied(unitCopy)).isTrue();
    }
}