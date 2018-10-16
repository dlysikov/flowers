package lu.luxtrust.flowers.controller.it;

import io.restassured.RestAssured;
import lu.luxtrust.flowers.FlowersApplication;
import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.common.Language;
import lu.luxtrust.flowers.entity.common.Nationality;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.properties.JWTProperties;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FlowersApplication.class)
@ActiveProfiles("test")
@Lazy
@Transactional
public abstract class AbstractIntegrationTestAPI {

    @LocalServerPort
    protected int port;
    @Autowired
    protected EntityManager entityManager;
    @Autowired
    private JWTProperties jwtProperties;

    public static final String VERY_LONG_STRING = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    public static final String CSD_SSN = "80101281985087818823";
    public static final String FLOWERS_ADMIN_SSN = "00101281985087818823";
    public static final String ESEAL_OFFICER_SSN = "50101281985087818823";
    public static final String ESEAL_MANAGER_SSN = "60101281985087818823";
    public static final String DIA_BTA_SSN = "30101281985087818823";
    public static final String DIA_DGSANTE_SSN = "40101281985087818823";

    private User csd;
    private User flowersAdmin;
    private User dgsanteDia;
    private User btaDia;
    private User eSealOfficer;
    private User eSealManager;

    private Requestor dgSante;
    private Requestor bta;

    private Country testCoutry;
    private Nationality testNationality;
    private Language testLanguage;

    private Unit testUnit;

    private Map<RoleType, Role> roles = new HashMap<>();

    @Before
    public void init() {
        RestAssured.baseURI = "https://localhost:" + port;
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.ssn = ?1", User.class);
        query.setParameter(1, CSD_SSN);
        csd = query.getSingleResult();

        query = entityManager.createQuery("select u from User u where u.ssn = ?1", User.class);
        query.setParameter(1, ESEAL_OFFICER_SSN);
        eSealOfficer = query.getSingleResult();

        query = entityManager.createQuery("select u from User u where u.ssn = ?1", User.class);
        query.setParameter(1, ESEAL_MANAGER_SSN);
        eSealManager = query.getSingleResult();


        query = entityManager.createQuery("select u from User u where u.ssn = ?1", User.class);
        query.setParameter(1, DIA_BTA_SSN);
        btaDia = query.getSingleResult();

        query = entityManager.createQuery("select u from User u where u.ssn = ?1", User.class);
        query.setParameter(1, FLOWERS_ADMIN_SSN);
        flowersAdmin = query.getSingleResult();

        query = entityManager.createQuery("select u from User u where u.ssn = ?1", User.class);
        query.setParameter(1, DIA_DGSANTE_SSN);
        dgsanteDia = query.getSingleResult();

        TypedQuery<Requestor> query1 = entityManager.createQuery("select s from Requestor s where s.name=?1", Requestor.class);
        query1.setParameter(1, "DG Sante");
        dgSante = query1.getSingleResult();

        query1 = entityManager.createQuery("select s from Requestor s where s.name=?1", Requestor.class);
        query1.setParameter(1, "BTA");
        bta = query1.getSingleResult();

        testCoutry = entityManager.createQuery("select c from Country c", Country.class).getResultList().get(0);
        testNationality = entityManager.createQuery("select n from Nationality n", Nationality.class).getResultList().get(0);
        testUnit = entityManager.createQuery("select u from Unit u", Unit.class).getResultList().get(0);
        testLanguage = entityManager.createQuery("select l from Language l", Language.class).getResultList().get(0);
        entityManager.createQuery("select r from Role r", Role.class).getResultList().forEach(role -> roles.put(role.getRoleType(), role));
    }

    String getAuthHeaderValue(String ssn) {
        return when().
                    get("/api/login/mock?ssn=" + ssn).
               then().
                    extract().
                    header(jwtProperties.getAuthHeader());
    }

    String getAuthHeaderName() {
        return jwtProperties.getAuthHeader();
    }

    Nationality getTestNationality() {
        return testNationality;
    }

    Country getTestCoutry() {
        return testCoutry;
    }

    Unit getTestUnit() {
        return testUnit;
    }

    User getCsd() {
        return csd;
    }

    User getBtaDia() {
        return btaDia;
    }

    User getFlowersAdmin() {
        return flowersAdmin;
    }

    User getDgsanteDia() {
        return dgsanteDia;
    }

    Requestor getBta() {
        return bta;
    }

    Requestor getDgSante() {
        return dgSante;
    }

    Language getTestLanguage() {
        return testLanguage;
    }

    Map<RoleType, Role> getRoles() {
        return roles;
    }

    User geteSealOfficer() {
        return eSealOfficer;
    }

    User geteSealManager() {
        return eSealManager;
    }
}
