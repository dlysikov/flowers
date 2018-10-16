package lu.luxtrust.flowers.controller.it;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import lu.luxtrust.flowers.entity.enrollment.*;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.CertificateType;
import lu.luxtrust.flowers.enums.Device;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.integration.lrs.mock.LrsWsMock;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.persistence.TypedQuery;
import java.io.File;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProCertificateOrderByFlowersAdminTestAPI extends AbstractOrderTestAPI {

    private static Long orderId;
    private static String authHeaderValue;

    @Autowired
    private LrsWsMock lrsWS;

    @Test
    public void test1CreateEmptyDraft() {
        User issuer = getCsd();

        CertificateOrder order = new CertificateOrder();
        order.setHolder(new Holder());
        order.setAddress(new Address());
        order.getHolder().setCertificateType(CertificateType.PROFESSIONAL_PERSON);

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
    }

    @Test
    public void test2ModifyDraftWithValidationViolations() {
        CertificateOrder order = entityManager.find(CertificateOrder.class, orderId);
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
                body("fieldErrors.size()", equalTo(17)).
                body("fieldErrors.find{it.field == 'tokenSerialNumber' && it.code == 'invalid'}.rejectedValue", equalTo(order.getTokenSerialNumber())).
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
    }

    @Test
    public void test3MoveToUserDraftWithValidationViolations() {
        test3MoveToUserDraftWithValidationViolations(orderId, authHeaderValue);
    }

    @Test
    public void test4MoveToUserDraftWithMinimumInfo() {
        test4MoveToUserDraftWithMinimumInfo(orderId, authHeaderValue, CertificateType.PROFESSIONAL_PERSON);
    }

    @Test
    public void test5UserValidatesWithViolations() {
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
    public void test6UserValidatesOrder() {
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
    public void test7SendToLrsWithValidationViolations() {
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
                body("fieldErrors.find{it.field == 'tokenSerialNumber' && it.code=='required'}.rejectedValue", nullValue());
    }

    @Test
    public void test8SendToLRSWithLRSErrors() {
        lrsWS.putLRSNumberForOrder(orderId.toString(), null);

        CertificateOrder order = entityManager.find(CertificateOrder.class, orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DIA_SIGNING_REQUIRED);

        given().
                body(order).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                post("/api/orders/" +orderId + "/send_to_lrs").
        then().
                statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).
                body("globalErrors.size()", equalTo(1)).
                body("globalErrors[0].details", equalTo("LRS_REGISTER_FAILURE"));

        lrsWS.removeLrsNumberForOrder(orderId.toString());
    }

    @Test
    public void test9SendToLrs() {
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
    public void test9WasSentToLrs() {
        CertificateOrder order = entityManager.find(CertificateOrder.class, orderId);
        assertThat(order.getStatus()).isGreaterThanOrEqualTo(OrderStatus.SENT_TO_LRS);
    }
}
