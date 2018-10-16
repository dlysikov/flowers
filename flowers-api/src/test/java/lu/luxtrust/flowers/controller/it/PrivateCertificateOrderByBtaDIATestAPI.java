package lu.luxtrust.flowers.controller.it;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import lu.luxtrust.flowers.entity.enrollment.Address;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.Holder;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.*;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrivateCertificateOrderByBtaDIATestAPI extends AbstractOrderTestAPI {

    private static Long orderId;
    private static String authHeaderValue;

    @Test
    public void test1SaveAndSendToLrs() {
        User issuer = getBtaDia();

        CertificateOrder order = new CertificateOrder();
        order.setHolder(new Holder());
        order.setAddress(new Address());
        order.getHolder().setCertificateType(CertificateType.PRIVATE);
        order.setDevice(Device.MOBILE);
        order.setUnit(getTestUnit());
        order.setDepartment("FSDFSDFS");
        order.setAcceptedGTC(Boolean.TRUE);
        order.getHolder().setFirstName("John");
        order.getHolder().setSurName("Smith");
        order.getHolder().setNationality(getTestNationality());
        order.getHolder().seteMail("test@gamilc.com");
        order.getHolder().seteMailSecond("test@gamilc.com");
        order.getHolder().setRoleType(RoleType.END_USER);
        order.getHolder().setBirthDate(new Date(LocalDate.of(1998, 10, 10).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        order.getHolder().setPhoneNumber("+30503567212");
        order.getHolder().setActivationCode("11234");
        order.getHolder().setNotifyEmail("11234@gmail.com");
        order.getHolder().setCertificateLevel(CertificateLevel.LCP);
        order.getAddress().setName("naem");
        order.getAddress().setCompany("company");
        order.getAddress().setStreetAndHouseNo("street and house no");
        order.getAddress().setAddressLine2("address line 2");
        order.getAddress().setPostCode("post code");
        order.getAddress().setCity("city");
        order.getAddress().setCountry(getTestCoutry());
        order.getUnit().setRequestor(getBta());

        authHeaderValue = getAuthHeaderValue(issuer.getSsn());

        orderId = given().
                body(order).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                put("/api/orders/send_to_lrs").
        then().
                statusCode(HttpStatus.OK.value()).
        extract().
                jsonPath().getLong("id");

    }

    @Test
    public void test2WasSentToLrs() {
        CertificateOrder order = entityManager.find(CertificateOrder.class, orderId);
        assertThat(order.getStatus()).isGreaterThanOrEqualTo(OrderStatus.SENT_TO_LRS);
    }
}
