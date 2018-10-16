package lu.luxtrust.flowers.controller.it;

import com.google.common.collect.Lists;
import io.restassured.http.ContentType;
import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.CompanyIdentifier;
import lu.luxtrust.flowers.entity.enrollment.Holder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.enums.CertificateType;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.enums.StatusAPI;
import lu.luxtrust.flowers.model.ValidatedList;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExternalApiTestAPI extends AbstractOrderTestAPI {

    private static final String EXT_ID_WITH_DOCS = "11111111";
    private static final String EXT_ID_WITHOUT_DOCS = "2222222";

    private static final String SSL_CLIENT_CERT = "-----BEGIN CERTIFICATE----- " +
            "MIIDPTCCAiWgAwIBAgIEGXLleDANBgkqhkiG9w0BAQsFADBPMQswCQYDVQQGEwJM " +
            "VTEWMBQGA1UEChMNTHV4VHJ1c3QgUy5BLjEaMBgGA1UECxMRSW50ZXJuYWwgVXNl " +
            "IE9ubHkxDDAKBgNVBAMTAzEyMzAeFw0xODAyMjQwOTMyMDNaFw0xODA1MjUwOTMy " +
            "MDNaME8xCzAJBgNVBAYTAkxVMRYwFAYDVQQKEw1MdXhUcnVzdCBTLkEuMRowGAYD " +
            "VQQLExFJbnRlcm5hbCBVc2UgT25seTEMMAoGA1UEAxMDMTIzMIIBIjANBgkqhkiG " +
            "9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr6Ay9FM/GsTy14OI/XSCWMHSNHFS7F/YMtzz " +
            "Cj+3WpKKIK4gSELM5rk456hwfZZC6jpb0WmBYM0831RK6EhoU3MrnZZdCG4KDQeB " +
            "J/0Gz6DTSr7kZNtZZ7Fl9H3Juqh6wSB8i4RnyuezVT6oC0DXSSjOdwFT6eZLJZ/1 " +
            "FsojzCJd6XkmugioCZYennBUF6vP/57cDdP/0tvP+t7zH+6y6B4x6Ft0areO5Nbc " +
            "7oxWiV+XypdshrLJBxvyCLqE/bjOqvK7U5wzQRU1d0KzX1TXhqiDvZZSNleXRiMN " +
            "RxC5ieSUaBheVKlO/BhseHn/gSK4UvFYCKnHhYYOx3BgACooYwIDAQABoyEwHzAd " +
            "BgNVHQ4EFgQUtos1mY9oc8jS2bSeF0xD/FMl9CMwDQYJKoZIhvcNAQELBQADggEB " +
            "AEEKFt+A27A9sFGaWTkPdTmlYQMTgOkxg+bEHqsUA01r+yqYN9nQxETcKaXbHmT3 " +
            "4+NhM8ENomfuY6lm1UQd0YvK2xPIW0TgyGe3E76iP6NlXz8So4O77NxsQkA+QxC4 " +
            "uDMTXlXdhFT9HSPHt5vu0Ga9ceJ4bVrx9u+dCIcsKkRkfTD1Uh1/Quyhfxf4gLsD " +
            "ajmWPFvk/FoBEDiGGDYFbhD0KYis53nzuIxd763V2q4/CJOMmcz47SgTx1h0VpDb " +
            "jGm9QLoPhZ+HMW1Fs+E9SAWDS3I40CrYOEVhuO1Th2P+QgUreUf2VPhIUkb4AB74 " +
            "+oK8C7HWwHUc8/dLChiNyiA= " +
            "-----END CERTIFICATE-----";

    @Value("${external.api.ssl_client_cert_header}")
    private String sslCertHeader;

    private static CertificateOrder withDocs;
    private static CertificateOrder withoutDocs;
    private static Unit unit;

    @BeforeClass
    public static void prepareOrders() {
        unit= new Unit();
        Requestor requestor = new Requestor();
        requestor.setCsn("426960248");
        requestor.setName("DG Sante");
        requestor.setId(1L);
        unit.setRequestor(requestor);
        CompanyIdentifier companyIdentifier = new CompanyIdentifier();
        companyIdentifier.setType(CompanyIdentifier.Type.OTHER);
        companyIdentifier.setValue("111");
        unit.setIdentifier(companyIdentifier);
        Country country = new Country();
        country.setCountryCode("ad");
        country.setId(1L);
        unit.setCountry(country);
        unit.setCity("Kiev");
        unit.setCommonName("Luxoft");
        unit.setCompanyName("Luxoft");
        unit.setPostCode("111");
        unit.setStreetAndHouseNo("qqq");

        withDocs = new CertificateOrder();
        withDocs.setHolder(new Holder());
        withDocs.getHolder().setFirstName("John");
        withDocs.getHolder().setSurName("Smith");
        withDocs.getHolder().setNotifyEmail("john.smith@gmail.com");
        withDocs.getHolder().setCertificateType(CertificateType.PRIVATE);
        withDocs.getHolder().setRoleType(RoleType.DIA);
        withDocs.getHolder().setPhoneNumber("+123456789123");
        withDocs.setUserExternalId(EXT_ID_WITH_DOCS);
        withDocs.getHolder().setWaitDocuments(Boolean.TRUE);
        withDocs.setUnit(unit);

        withoutDocs = new CertificateOrder();
        withoutDocs.setHolder(new Holder());
        withoutDocs.getHolder().setFirstName("Jane");
        withoutDocs.getHolder().setSurName("Smith");
        withoutDocs.getHolder().setNotifyEmail("jane.smith@gmail.com");
        withoutDocs.getHolder().setCertificateType(CertificateType.PRIVATE);
        withoutDocs.getHolder().setRoleType(RoleType.DIA);
        withoutDocs.getHolder().setPhoneNumber("+123456789123");
        withoutDocs.setUserExternalId(EXT_ID_WITHOUT_DOCS);
        withoutDocs.setUnit(unit);
    }

    @Test
    public void test1SendOrdersByAPIWithValidationViolations() {
        CertificateOrder order = new CertificateOrder();
        order.setHolder(new Holder());
        order.getHolder().setWaitDocuments(Boolean.TRUE);
        order.setUnit(new Unit());
        order.getUnit().setCountry(new Country());
        order.getUnit().setIdentifier(new CompanyIdentifier());
        order.getUnit().setRequestor(new Requestor());

        given().
                body(new ValidatedList<>(Lists.newArrayList(order))).
                contentType(ContentType.JSON).
                header(sslCertHeader, SSL_CLIENT_CERT).
        when().
                put("/api/external/orders").
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", is(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(15)).
                body("fieldErrors.find{it.field=='holder.firstName' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='holder.surName' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='holder.phoneNumber' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='holder.notifyEmail' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='userExternalId' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='certificateType' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='roleType' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='unit' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='unit.requestor' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='unit.country' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='unit.postCode' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='unit.city' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='unit.streetAndHouseNo' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='unit.companyName' && it.code=='required'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.field=='unit.commonName' && it.code=='required'}.rejectedValue", nullValue())
        ;
    }

    @Test
    public void test2SendOrdersByAPI() {
        given().
                body(new ValidatedList<>(Lists.newArrayList(withDocs, withoutDocs))).
                contentType(ContentType.JSON).
                header(sslCertHeader, SSL_CLIENT_CERT).
        when().
                put("/api/external/orders").
        then().
                statusCode(HttpStatus.OK.value()).
                body("size()", equalTo(2)).
                body("find{it.userExternalId == '" + withDocs.getUserExternalId() + "'}.status", equalTo(StatusAPI.WAITING_FOR_DOCUMENTS.toString())).
                body("find{it.userExternalId == '" + withoutDocs.getUserExternalId() + "'}.status", equalTo(StatusAPI.ORDER_SAVED.toString()));
    }

    @Test
    public void test3SendOrderByAPIWithDuplicates() {
        given().
                body(new ValidatedList<>(Lists.newArrayList(withDocs, withoutDocs))).
                contentType(ContentType.JSON).
                header(sslCertHeader, SSL_CLIENT_CERT).
        when().
                put("/api/external/orders").
        then().
                statusCode(HttpStatus.OK.value()).
                body("size()", equalTo(2)).
                body("find{it.userExternalId == '" + withDocs.getUserExternalId() + "'}.status", equalTo(StatusAPI.DUPLICATE.toString())).
                body("find{it.userExternalId == '" + withoutDocs.getUserExternalId() + "'}.status", equalTo(StatusAPI.DUPLICATE.toString()));
    }

    @Test
    public void test4SendDocsByAPIWhenTheyAreNotExpected() {
        File doc = new File(getClass().getResource("/user_document.pdf").getPath());

        given().
                multiPart("file[0]", doc).
                contentType("multipart/form-data").
                header(sslCertHeader, SSL_CLIENT_CERT).
        when().
                post("/api/external/orders/" + withoutDocs.getUserExternalId() + "/document").
        then().
                statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void test5SendDocsByAPI() {
        File doc = new File(getClass().getResource("/user_document.pdf").getPath());

        given().
                multiPart("file[0]", doc).
                contentType("multipart/form-data").
                header(sslCertHeader, SSL_CLIENT_CERT).
        when().
                post("/api/external/orders/" + withDocs.getUserExternalId() + "/document").
        then().
                statusCode(HttpStatus.OK.value()).
                body("status", equalTo(StatusAPI.DOCUMENTS_LOADED.toString())).
                body("userExternalId", equalTo(withDocs.getUserExternalId()));
    }

    @Test
    public void test6SendToMuchOrdersByAPI() {
        List<CertificateOrder> os = new ArrayList<>();
        for (int i = 0;i < 1001;i++) {
            CertificateOrder o = new CertificateOrder();
            o.setHolder(new Holder());
            o.getHolder().setFirstName("John" + i);
            o.getHolder().setSurName("Smith");
            o.getHolder().setNotifyEmail("john" + i + ".smith@gmail.com");
            o.getHolder().setRoleType(RoleType.DIA);
            o.getHolder().setPhoneNumber("+123456789123");
            o.getHolder().setCertificateType(CertificateType.PRIVATE);
            o.setUserExternalId(EXT_ID_WITH_DOCS + i);
            o.getHolder().setWaitDocuments(Boolean.TRUE);
            o.setUnit(unit);
            os.add(o);
        }

        given().
                body(new ValidatedList<>(os)).
                contentType(ContentType.JSON).
                header(sslCertHeader, SSL_CLIENT_CERT).
        when().
                put("/api/external/orders").
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.size()", equalTo(1)).
                body("globalErrors[0].code", equalTo("items.length.exceeded")).
                body("fieldErrors.isEmpty()", is(true));
    }


}