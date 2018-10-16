package lu.luxtrust.flowers.controller.it;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import lu.luxtrust.flowers.entity.builder.UserBuilder;
import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.CertificateType;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.repository.UserRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Rollback(false)
public class UserTestAPI extends AbstractIntegrationTestAPI {

    private static String authHeaderValue;
    private static Long userId;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test01SaveWithoutValidCredentials() {
        User user = getUser();
        given().
                body(user).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), getAuthHeaderValue(getBtaDia().getSsn()))).
                when().
                put("/api/users").
                then().
                statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void test02SaveWithValidationViolations() {
        User user = UserBuilder.newBuilder()
                .birthDate(null)
                .certificateType(null)
                .defaultLanguage(null)
                .email(null)
                .firstName(null)
                .surname(null)
                .nationality(null)
                .unit(null)
                .ssn(null)
                .roles(null)
                .build();

        authHeaderValue = getAuthHeaderValue(getFlowersAdmin().getSsn());

        given().
                body(user).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
                when().
                put("/api/users").
                then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", equalTo(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(7)).
                body("fieldErrors.find{it.code == 'required' && it.field =='defaultLanguage'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='certificateType'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='email'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='firstName'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='surname'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='ssn'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='roles'}.rejectedValue", nullValue());

        user.setSsn(VERY_LONG_STRING);
        user.setFirstName(VERY_LONG_STRING);
        user.setSurName(VERY_LONG_STRING);
        user.setEmail(VERY_LONG_STRING);
        user.setCertificateType(CertificateType.PRIVATE);
        user.setRoles(Collections.singleton(getRoles().get(RoleType.DIA)));
        user.setDefaultLanguage(getTestLanguage());

        given().
                body(user).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
                when().
                put("/api/users").
                then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", equalTo(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(6)).
                body("fieldErrors.find{it.code == 'length' && it.field == 'ssn'}.rejectedValue", equalTo(VERY_LONG_STRING)).
                body("fieldErrors.find{it.code == 'length' && it.field == 'firstName'}.rejectedValue", equalTo(VERY_LONG_STRING)).
                body("fieldErrors.find{it.code == 'length' && it.field == 'surName'}.rejectedValue", equalTo(VERY_LONG_STRING)).
                body("fieldErrors.find{it.code == 'length' && it.field == 'email'}.rejectedValue", equalTo(VERY_LONG_STRING)).
                body("fieldErrors.find{it.code == 'invalid' && it.field =='email'}.rejectedValue", equalTo(VERY_LONG_STRING)).
                body("fieldErrors.find{it.code == 'required' && it.field =='unit'}.rejectedValue", nullValue());
    }

    @Test
    public void test03Save() {
        User user = getUser();

        userId = given().
                body(user).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
                when().
                put("/api/users").
                then().
                statusCode(HttpStatus.OK.value()).
                extract().
                jsonPath().getLong("id");
    }

    @Test
    public void test04CreateDuplicate() {
        User user = getUser();

        given().
                body(user).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
                when().
                put("/api/users").
                then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", equalTo(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(1)).
                body("fieldErrors.find{it.code == 'duplicate' && it.field == 'ssn'}.rejectedValue", equalTo(user.getSsn()));
    }

    @Test
    public void test05UpdateUser() {
        User user = getUser();
        user.setId(userId);

        given().
                body(user).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
                when().
                post("/api/users/" + user.getId()).
                then().
                statusCode(HttpStatus.OK.value());
    }

    @Test
    public void test06CreateRestrictedToManage() {
        User user = getUser();
        user.setSsn("234234234234");
        Role role = new Role();
        role.setRoleType(RoleType.ESEAL_MANAGER);
        user.setRoles(Collections.singleton(role));
        user.setNationality(getTestNationality());

        given().
                body(user).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
                when().
                put("/api/users").
                then().
                statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void test07FindAllUsers() {
        Long usersCount = userRepository.count();

        given().
                header(new Header(getAuthHeaderName(), authHeaderValue)).
                when().
                get("/api/users").
                then().
                statusCode(HttpStatus.OK.value()).
                body("data.size()", equalTo(usersCount.intValue())).
                body("totalCount", equalTo(usersCount.intValue())).
                body("data.find{it.id == " + userId.intValue() + "}.id", equalTo(userId.intValue()));
    }

    private User getUser() {
        return UserBuilder.newBuilder()
                .roles(Collections.singleton(getRoles().get(RoleType.DIA)))
                .defaultLanguage(getTestLanguage())
                .unit(getTestUnit())
                .build();
    }

}
