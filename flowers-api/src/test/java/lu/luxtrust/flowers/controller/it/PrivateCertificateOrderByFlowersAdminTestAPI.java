package lu.luxtrust.flowers.controller.it;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import lu.luxtrust.flowers.entity.enrollment.Address;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.Holder;
import lu.luxtrust.flowers.entity.enrollment.OrderUserValidatePage;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.CertificateType;
import lu.luxtrust.flowers.enums.Device;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.model.OrderUserPageMobileCodeVerification;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.HttpStatus;

import javax.persistence.TypedQuery;
import java.io.File;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrivateCertificateOrderByFlowersAdminTestAPI extends AbstractOrderTestAPI {

    private static Long orderId;
    private static String authHeaderValue;

    @Test
    public void test01CreateEmptyDraft() {
        User issuer = getCsd();

        CertificateOrder order = new CertificateOrder();
        order.setHolder(new Holder());
        order.setAddress(new Address());
        order.getHolder().setCertificateType(CertificateType.PRIVATE);
        order.getUnit().getRequestor().getConfig().setCsdEnvolvement(false);

        authHeaderValue = getAuthHeaderValue(issuer.getSsn());

        orderId = given().
                body(order).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                put("/api/orders").
        then().
                statusCode(HttpStatus.OK.value()).
        extract().
                jsonPath().getLong("id");

        CertificateOrder actual = entityManager.find(CertificateOrder.class, orderId);
        assertThat(actual).isNotNull();
        assertThat(actual.getIssuer()).isNotNull();
        assertThat(actual.getIssuer().getSsn()).isEqualTo(issuer.getSsn());
        assertThat(actual.getRequestDate()).isNotNull();
    }

    @Test
    public void test02ModifyDraftWithValidationViolations() {
        CertificateOrder order = entityManager.find(CertificateOrder.class, orderId);
        order.setDevice(Device.TOKEN);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DRAFT);

        spoilDraftOrder(order);

        given().
                body(order).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                post("/api/orders/" + orderId).
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", is(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(18)).
                body("fieldErrors.find{it.field == 'tokenSerialNumber' && it.code == 'invalid'}.rejectedValue", equalTo(order.getTokenSerialNumber())).
                body("fieldErrors.find{it.field == 'department' && it.code == 'length'}.rejectedValue", equalTo(order.getDepartment())).
                body("fieldErrors.find{it.field == 'holder.firstName' && it.code == 'length'}.rejectedValue", equalTo(order.getHolder().getFirstName())).
                body("fieldErrors.find{it.field == 'holder.surName' && it.code == 'length'}.rejectedValue", equalTo(order.getHolder().getSurName())).
                body("fieldErrors.find{it.field == 'holder.eMail' && it.code == 'length'}.rejectedValue", equalTo(order.getHolder().geteMail())).
                body("fieldErrors.find{it.field == 'holder.eMail' && it.code == 'invalid'}.rejectedValue", equalTo(order.getHolder().geteMail())).
                body("fieldErrors.find{it.field == 'holder.eMailSecond' && it.code == 'length'}.rejectedValue", equalTo(order.getHolder().geteMailSecond())).
                body("fieldErrors.find{it.field == 'holder.eMailSecond' && it.code == 'invalid'}.rejectedValue", equalTo(order.getHolder().geteMailSecond())).
                body("fieldErrors.find{it.field == 'holder.activationCode' && it.code == 'length'}.rejectedValue", equalTo(order.getHolder().getActivationCode())).
                body("fieldErrors.find{it.field == 'holder.phoneNumber' && it.code == 'invalid'}.rejectedValue", equalTo(order.getHolder().getPhoneNumber())).
                body("fieldErrors.find{it.field == 'holder.notifyEmail' && it.code == 'length'}.rejectedValue", equalTo(order.getHolder().getNotifyEmail())).
                body("fieldErrors.find{it.field == 'holder.notifyEmail' && it.code == 'invalid'}.rejectedValue", equalTo(order.getHolder().getNotifyEmail())).
                body("fieldErrors.find{it.field == 'address.name' && it.code == 'length'}.rejectedValue", equalTo(order.getAddress().getName())).
                body("fieldErrors.find{it.field == 'address.company' && it.code == 'length'}.rejectedValue", equalTo(order.getAddress().getCompany())).
                body("fieldErrors.find{it.field == 'address.streetAndHouseNo' && it.code == 'length'}.rejectedValue", equalTo(order.getAddress().getStreetAndHouseNo())).
                body("fieldErrors.find{it.field == 'address.addressLine2' && it.code == 'length'}.rejectedValue", equalTo(order.getAddress().getAddressLine2())).
                body("fieldErrors.find{it.field == 'address.postCode' && it.code == 'length'}.rejectedValue", equalTo(order.getAddress().getPostCode())).
                body("fieldErrors.find{it.field == 'address.city' && it.code == 'length'}.rejectedValue", equalTo(order.getAddress().getCity()));
    }

    private void spoilDraftOrder(CertificateOrder order) {
        order.setDevice(Device.TOKEN);
        order.setTokenSerialNumber("23423523535");
        order.getHolder().setFirstName(VERY_LONG_STRING);
        order.getHolder().setSurName(VERY_LONG_STRING);
        order.getHolder().seteMail(VERY_LONG_STRING);
        order.getHolder().seteMailSecond(VERY_LONG_STRING);
        order.getHolder().setActivationCode(VERY_LONG_STRING);
        order.getHolder().setPhoneNumber(VERY_LONG_STRING);
        order.getHolder().setNotifyEmail(VERY_LONG_STRING);
        order.getAddress().setName(VERY_LONG_STRING);
        order.getAddress().setCompany(VERY_LONG_STRING);
        order.getAddress().setStreetAndHouseNo(VERY_LONG_STRING);
        order.getAddress().setAddressLine2(VERY_LONG_STRING);
        order.getAddress().setPostCode(VERY_LONG_STRING);
        order.getAddress().setCity(VERY_LONG_STRING);
        order.setDepartment(VERY_LONG_STRING);
    }

    @Test
    public void test03MoveToUserDraftWithValidationViolations() {
        test3MoveToUserDraftWithValidationViolations(orderId, authHeaderValue);
    }

    @Test
    public void test04MoveToUserDraftWithMinimumInfo() {
        test4MoveToUserDraftWithMinimumInfo(orderId, authHeaderValue, CertificateType.PRIVATE);
    }

    @Test
    public void test05CheckWhetherAuthCodeRequired() {
        TypedQuery<String> query = entityManager.createQuery("select p.pageHash from OrderUserValidatePage p where p.order.id=?1", String.class);
        query.setParameter(1, orderId);
        String page_hash = query.getSingleResult();
        when().
                get("/api/orders/user/validate/is_mobile_code_required/" + page_hash).
        then().
                statusCode(HttpStatus.OK.value()).
                body(equalTo(Boolean.FALSE.toString()));
    }

    @Test
    public void test06VerifyMobileCode() {
        TypedQuery<OrderUserValidatePage> query = entityManager.createQuery("select p from OrderUserValidatePage p where p.order.id=?1", OrderUserValidatePage.class);
        query.setParameter(1, orderId);
        OrderUserValidatePage validatePage = query.getSingleResult();

        OrderUserPageMobileCodeVerification verification = new OrderUserPageMobileCodeVerification();
        verification.setPageHash(validatePage.getPageHash());
        verification.setMobileCode("1231456");

        given().
                body(verification).
                contentType(ContentType.JSON).
        when().
                post("/api/orders/user/validate/verify_mobile_code").
        then().
                statusCode(HttpStatus.OK.value()).
                body(equalTo(Boolean.FALSE.toString()));

        given().
                body(new OrderUserPageMobileCodeVerification()).
                contentType(ContentType.JSON).
        when().
                post("/api/orders/user/validate/verify_mobile_code").
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", is(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(2)).
                body("fieldErrors.find{it.field == 'pageHash' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'mobileCode' && it.code=='required'}.rejectedValue", nullValue());

        verification.setMobileCode("sfsd");
        verification.setPageHash("sfsd");

        given().
                body(verification).
                contentType(ContentType.JSON).
        when().
                post("/api/orders/user/validate/verify_mobile_code").
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", is(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(2)).
                body("fieldErrors.find{it.field == 'pageHash' && it.code=='length'}.rejectedValue", equalTo(verification.getPageHash())).
                body("fieldErrors.find{it.field == 'mobileCode' && it.code=='length'}.rejectedValue", equalTo(verification.getMobileCode()));

        verification.setMobileCode(validatePage.getMobileValidationCode());
        verification.setPageHash(validatePage.getPageHash());

        given().
                body(verification).
                contentType(ContentType.JSON).
        when().
                post("/api/orders/user/validate/verify_mobile_code").
        then().
                statusCode(HttpStatus.OK.value()).
                body(equalTo(Boolean.TRUE.toString()));;
    }

    @Test
    public void test07UserValidatesWithViolations() {
        CertificateOrder order = entityManager.find(CertificateOrder.class, orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.USER_DRAFT);
        spoilUserDraftOrder(order);

        TypedQuery<OrderUserValidatePage> query = entityManager.createQuery("select p from OrderUserValidatePage p where p.order.id=?1", OrderUserValidatePage.class);
        query.setParameter(1, orderId);
        OrderUserValidatePage validatePage = query.getSingleResult();
        assertThat(validatePage).isNotNull();

        given().
                body(order).
                contentType(ContentType.JSON).
        when().
                post("/api/orders/user/validate/" + validatePage.getPageHash()).
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", is(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(10)).
                body("fieldErrors.find{it.field == 'device' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'acceptedGTC' && it.code=='required'}.rejectedValue", is(order.getAcceptedGTC())).
                body("fieldErrors.find{it.field == 'holder.nationality' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'holder.activationCode' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'holder.documents' && it.code=='required'}", notNullValue()).
                body("fieldErrors.find{it.field == 'address.streetAndHouseNo' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'address.city' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'address.country' && it.code=='required'}.rejectedValue", nullValue());

        order.getHolder().setActivationCode("444");
        given().
                body(order).
                contentType(ContentType.JSON).
        when().
                post("/api/orders/user/validate/" + validatePage.getPageHash()).
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", is(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(10)).
                body("fieldErrors.find{it.field=='holder.activationCode' && it.code=='length'}.rejectedValue", equalTo(order.getHolder().getActivationCode()));
    }

    private void spoilUserDraftOrder(CertificateOrder order) {
        order.setDevice(null);
        order.setTokenSerialNumber(null);
        order.setAcceptedGTC(Boolean.FALSE);
        order.getHolder().setNationality(null);
        order.getHolder().setActivationCode(null);
        order.getHolder().setPhoneNumber(null);
        order.getAddress().setStreetAndHouseNo(null);
        order.getAddress().setPostCode(null);
        order.getAddress().setCity(null);
        order.getAddress().setCountry(null);
    }

    @Test
    public void test08UserValidatesOrder() {
        CertificateOrder order = entityManager.find(CertificateOrder.class, orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.USER_DRAFT);

        TypedQuery<OrderUserValidatePage> query = entityManager.createQuery("select p from OrderUserValidatePage p where p.order.id=?1", OrderUserValidatePage.class);
        query.setParameter(1, orderId);
        OrderUserValidatePage validatePage = query.getSingleResult();
        assertThat(validatePage).isNotNull();

        File doc = new File(getClass().getResource("/user_document.pdf").getPath());

        given().
                multiPart("file[0]", doc).
                contentType("multipart/form-data").
        when().
                post("/api/orders/user/validate/" + validatePage.getPageHash() + "/document").
        then().
                statusCode(HttpStatus.OK.value());

        order.setDevice(Device.TOKEN);
        order.setTokenSerialNumber("11-1111111-1");
        order.setAcceptedGTC(Boolean.TRUE);
        order.getHolder().setNationality(getTestNationality());
        order.getHolder().setActivationCode("12345");
        order.getHolder().setPhoneNumber("+380603967156");
        order.getAddress().setStreetAndHouseNo("Radysheva 10");
        order.getAddress().setPostCode("234324234");
        order.getAddress().setCity("Kiev");
        order.getAddress().setCountry(getTestCoutry());

        given().
                body(order).
                contentType(ContentType.JSON).
        when().
                post("/api/orders/user/validate/" + validatePage.getPageHash()).
        then().
                statusCode(HttpStatus.OK.value());

        when().
                get("/api/orders/user/validate/" + validatePage.getPageHash()).
        then().
                statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void test09SendToLrsWithValidationViolations() {
        CertificateOrder order = entityManager.find(CertificateOrder.class, orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DIA_SIGNING_REQUIRED);

        order.setTokenSerialNumber(null);
        order.setDevice(Device.TOKEN);

        given().
                body(order).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                post("/api/orders/" +orderId + "/send_to_lrs").
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", is(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(1)).
                body("fieldErrors[0].field", equalTo("tokenSerialNumber")).
                body("fieldErrors[0].code", equalTo("required"));
    }

    @Test
    public void test10SendToLrs() {
        CertificateOrder order = entityManager.find(CertificateOrder.class, orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DIA_SIGNING_REQUIRED);

        given().
                body(order).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                post("/api/orders/" +orderId + "/send_to_lrs").
        then().
                statusCode(HttpStatus.OK.value());
    }

    @Test
    public void test11WasSentToLrs() {
        CertificateOrder order = entityManager.find(CertificateOrder.class, orderId);
        assertThat(order.getStatus()).isGreaterThanOrEqualTo(OrderStatus.SENT_TO_LRS);
    }
}
