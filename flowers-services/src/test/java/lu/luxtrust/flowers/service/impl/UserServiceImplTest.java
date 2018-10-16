package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.AbstractWithSpringContextTest;
import lu.luxtrust.flowers.entity.builder.UnitBuilder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.entity.builder.RequestorBuilder;
import lu.luxtrust.flowers.entity.builder.UserBuilder;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.model.FieldFilter;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceImplTest  extends AbstractWithSpringContextTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TestEntityManager entityManager;
    private Role dia;
    private Role flowersAdmin;
    private Role csdAgent;
    private Role esealOfficer;
    private Role esealManager;
    private Requestor requestor;
    private Unit unit;
    private Unit unit2;

    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @Before
    public void init() {
        flowersAdmin = initRole(RoleType.FLOWERS_ADMIN);
        dia = initRole(RoleType.DIA);
        csdAgent = initRole(RoleType.CSD_AGENT);
        esealOfficer = initRole(RoleType.ESEAL_OFFICER);
        esealManager = initRole(RoleType.ESEAL_MANAGER);

        requestor = RequestorBuilder.newBuilder().build();
        entityManager.persist(requestor);

        unit = UnitBuilder.newBuilder().requestor(requestor).build();
        entityManager.persist(unit.getCountry());
        entityManager.persist(unit);

        unit2 = UnitBuilder.newBuilder().requestor(requestor).country(unit.getCountry()).build();
        entityManager.persist(unit2);

        user1 = UserBuilder.newBuilder().email("aa@gmail.com").ssn("aaaa").roles(Collections.singleton(flowersAdmin)).build();
        user2 = UserBuilder.newBuilder().email("aa1@gmail.com").ssn("aaaa1").roles(Collections.singleton(flowersAdmin)).build();
        user3 = UserBuilder.newBuilder().email("aa2@gmail.com").unit(unit).ssn("aaaa2").roles(Collections.singleton(dia)).build();
        user4 = UserBuilder.newBuilder().email("aa3@gmail.com").unit(unit2).ssn("aaaa3").roles(Collections.singleton(dia)).build();

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.persist(user4);
    }

    private Role initRole(RoleType roleType) {
        Role role = new Role();
        role.setRoleType(roleType);
        return entityManager.persist(role);
    }

    @Test
    public void findEmailsByRole() {
        assertThat(userService.findEmailsByRole(RoleType.FLOWERS_ADMIN)).containsExactlyInAnyOrder("aa@gmail.com", "aa1@gmail.com");
    }

    @Test
    public void findDiaEmailsByUnit() {
        assertThat(userService.findDiaEmailsByUnit(unit)).containsExactlyInAnyOrder("aa2@gmail.com");
    }

    @Test
    public void findActiveBySSN() {
        assertThat(userService.findActiveBySSN(user1.getSsn())).isEqualTo(user1);
    }

    @Test
    public void managedBy() {
        assertThat(userService.managedBy(Collections.singletonList(RoleType.FLOWERS_ADMIN))).containsExactlyInAnyOrder(esealOfficer, dia, csdAgent, flowersAdmin);
        assertThat(userService.managedBy(Collections.singletonList(RoleType.ESEAL_OFFICER))).containsExactlyInAnyOrder(esealManager);
        assertThat(userService.managedBy(Collections.singletonList(RoleType.CSD_AGENT))).isEmpty();
        assertThat(userService.managedBy(Collections.singletonList(RoleType.DIA))).isEmpty();
        assertThat(userService.managedBy(Collections.singletonList(RoleType.ESEAL_MANAGER))).isEmpty();
        assertThat(userService.managedBy(Collections.singletonList(RoleType.END_USER))).isEmpty();
        assertThat(userService.managedBy(Collections.emptyList())).isEmpty();

        assertThat(userService.managedBy(Arrays.asList(RoleType.FLOWERS_ADMIN, RoleType.ESEAL_OFFICER))).containsExactlyInAnyOrder(esealManager, esealOfficer, dia, csdAgent, flowersAdmin);
    }

    @Test
    public void findAll() {
        PageParams params = new PageParams();
        params.setPageNumber(1);
        params.setPageSize(20);
        assertThat(userService.findAll(params, Collections.singletonList(flowersAdmin))).containsExactly(user2, user1);
        assertThat(userService.findAll(params, Collections.singletonList(dia))).containsExactly(user4, user3);
        assertThat(userService.findAll(params, Arrays.asList(flowersAdmin, dia))).containsExactly(user4, user3, user2, user1);
    }

    @Test
    public void count() {
        List<FieldFilter> filters = Collections.emptyList();
        assertThat(userService.count(filters, Collections.singletonList(flowersAdmin))).isEqualTo(2);
        assertThat(userService.count(filters, Collections.singletonList(dia))).isEqualTo(2);
        assertThat(userService.count(filters, Arrays.asList(flowersAdmin, dia))).isEqualTo(4);

        FieldFilter fieldFilter = new FieldFilter();
        fieldFilter.setField("ssn");
        fieldFilter.setType(FieldFilter.ValueType.STRING_LIKE);
        fieldFilter.setValue(Collections.singletonList("aaaa3"));
        assertThat(userService.count(Collections.singletonList(fieldFilter), Arrays.asList(flowersAdmin, dia))).isEqualTo(1);
    }

}