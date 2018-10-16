package lu.luxtrust.flowers.controller.it;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.CertificateLevel;
import lu.luxtrust.flowers.enums.CertificateType;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.enums.RoleType;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;

public abstract class AbstractOrderTestAPI extends AbstractIntegrationTestAPI {

    public void test3MoveToUserDraftWithValidationViolations(Long orderId, String authHeaderValue) {
        CertificateOrder order = entityManager.find(CertificateOrder.class, orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DRAFT);
        spoilDraftOrderBeforeSendToUser(order);

        given().
                body(order).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                post("/api/orders/" + orderId  + "/submit").
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", is(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(9)).
                body("fieldErrors.find{it.field == 'unit' && it.code == 'required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'holder.firstName' && it.code == 'required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'holder.phoneNumber' && it.code == 'required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'holder.firstName' && it.code == 'required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'holder.surName' && it.code == 'required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'holder.certificateType' && it.code == 'required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'holder.roleType' && it.code == 'required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'holder.birthDate' && it.code == 'required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'holder.notifyEmail' && it.code == 'required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field == 'holder.certificateLevel' && it.code == 'required'}.rejectedValue", nullValue());
    }

    private void spoilDraftOrderBeforeSendToUser(CertificateOrder order) {
        order.getHolder().setFirstName(null);
        order.getHolder().setSurName(null);
        order.getHolder().setCertificateType(null);
        order.getHolder().setRoleType(null);
        order.getHolder().setBirthDate(null);
        order.getHolder().setNotifyEmail(null);
        order.getHolder().setCertificateLevel(null);
    }

    public void test4MoveToUserDraftWithMinimumInfo(Long orderId, String authHeaderValue, CertificateType certificateType) {
        CertificateOrder order = entityManager.find(CertificateOrder.class, orderId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DRAFT);

        order.setUnit(getTestUnit());
        order.setDepartment("Department");
        order.getHolder().setFirstName("John");
        order.getHolder().setSurName("Smith");
        order.getHolder().setCertificateType(certificateType);
        order.getHolder().setPhoneNumber("+380503961212");
        order.getHolder().setRoleType(RoleType.END_USER);
        order.getHolder().setBirthDate(new Date(LocalDate.of(1998, 10, 10).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        order.getHolder().setNotifyEmail("test@gmail.com");
        order.getHolder().setCertificateLevel(CertificateLevel.LCP);

        given().
                body(order).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                post("/api/orders/" + orderId  + "/submit").
        then().
                statusCode(HttpStatus.OK.value());
    }
}
