package lu.luxtrust.flowers.controller.it;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import lu.luxtrust.flowers.entity.enrollment.Address;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.entity.enrollment.Holder;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.CertificateType;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.integration.lrs.mock.LrsWsMock;
import lu.luxtrust.flowers.repository.ESealOrderRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ESealCertificateTestAPI extends AbstractOrderTestAPI {

    private static Long orderId;
    private static String authHeaderValue;

    @Autowired private LrsWsMock lrsWS;

    @Test
    public void test1CreateEmptyDraft() {
        User issuer = geteSealOfficer();

        ESealOrder order = new ESealOrder();

        authHeaderValue = getAuthHeaderValue(issuer.getSsn());

        orderId = given().
            body(order).
            contentType(ContentType.JSON).
            header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
            post("/api/eseal_order/draft").
        then().
            statusCode(HttpStatus.OK.value()).
            extract().
            jsonPath().getLong("id");
    }

    @Test
    public void test2ModifyDraftWithValidationViolations() {
        ESealOrder order = entityManager.find(ESealOrder.class, orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DRAFT);

        order.seteSealAdministratorEmail(VERY_LONG_STRING);
        order.setOrganisationalUnit(VERY_LONG_STRING);

        given().
            body(order).
            contentType(ContentType.JSON).
            header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
            post("/api/eseal_order/draft").
        then().
            statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
            body("globalErrors.isEmpty()", is(Boolean.TRUE)).
            body("fieldErrors.size()", equalTo(3)).
            body("fieldErrors.find{it.field == 'eSealAdministratorEmail' && it.code == 'length'}.rejectedValue", equalTo(VERY_LONG_STRING)).
            body("fieldErrors.find{it.field == 'eSealAdministratorEmail' && it.code == 'invalid'}.rejectedValue", equalTo(VERY_LONG_STRING)).
            body("fieldErrors.find{it.field == 'organisationalUnit' && it.code == 'length'}.rejectedValue", equalTo(VERY_LONG_STRING));
    }

    @Test
    public void test3SendToLrsWithValidationViolations() {
        ESealOrder order = entityManager.find(ESealOrder.class, orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DRAFT);

        given().
            body(order).
            contentType(ContentType.JSON).
            header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
            post("/api/eseal_order/send_to_lrs").
        then().
            statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
            body("globalErrors.isEmpty()", is(Boolean.TRUE)).
            body("fieldErrors.size()", equalTo(4)).
            body("fieldErrors.find{it.field == 'eSealAdministratorEmail' && it.code == 'required'}.rejectedValue", nullValue()).
            body("fieldErrors.find{it.field == 'acceptedGTC' && it.code == 'required'}.rejectedValue", equalTo(order.getAcceptedGTC())).
            body("fieldErrors.find{it.field == 'unit' && it.code == 'required'}.rejectedValue", nullValue()).
            body("fieldErrors.find{it.field == 'eSealManagers' && it.code == 'required'}.rejectedValue", is(order.geteSealManagers()));
    }

    @Test
    public void test4SendToLrs() {
        ESealOrder order = entityManager.find(ESealOrder.class, orderId);
        order.setAcceptedGTC(Boolean.TRUE);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DRAFT);

        order.seteSealAdministratorEmail("qwe@qwe.com");
        order.setUnit(getTestUnit());
        order.seteSealManagers(Collections.singletonList(geteSealManager()));

        given().
            body(order).
            contentType(ContentType.JSON).
            header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
            post("/api/eseal_order/send_to_lrs").
        then().
            statusCode(HttpStatus.OK.value());
    }

    @Test
    public void test5WasSentToLrs() {
        ESealOrder order = entityManager.find(ESealOrder.class, orderId);
        assertThat(order.getStatus()).isGreaterThanOrEqualTo(OrderStatus.SENT_TO_LRS);
    }

    @Test
    public void test6GetList() {
        given().
            header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
            get("/api/eseal_order").
        then().
            statusCode(HttpStatus.OK.value()).
            body("data.size()", equalTo(1)).
            body("totalCount", equalTo(1)).
            body("data.find{it.id == " + orderId.intValue() + "}.id", equalTo(orderId.intValue()));
    }

    @Test
    public void test7GetOne() {
        given().
            header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
            get("/api/eseal_order/" + orderId).
        then().
            statusCode(HttpStatus.OK.value());
    }

    @Test
    public void test8ModifyAfterSendToLrsWithValidationViolations() {
        ESealOrder order = entityManager.find(ESealOrder.class, orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SENT_TO_LRS);

        order.seteSealAdministratorEmail(null);
        order.seteSealManagers(Collections.emptyList());

        given().
                body(order).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
                when().
                post("/api/eseal_order").
                then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", is(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(2)).
                body("fieldErrors.find{it.field == 'eSealAdministratorEmail' && it.code == 'required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'eSealManagers' && it.code == 'required'}.rejectedValue", is(order.geteSealManagers()));
    }

}
