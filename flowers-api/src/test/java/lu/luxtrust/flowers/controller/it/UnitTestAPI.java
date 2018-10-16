package lu.luxtrust.flowers.controller.it;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import lu.luxtrust.flowers.entity.builder.UnitBuilder;
import lu.luxtrust.flowers.entity.enrollment.CompanyIdentifier;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.repository.UnitRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Rollback(false)
public class UnitTestAPI extends AbstractIntegrationTestAPI {

    @Autowired
    private UnitRepository unitRepository;

    private static String authHeaderValue;
    private static Long unitId;

    @Test
    public void test01SaveWithoutValidCredentials() {
        given().
                body(UnitBuilder.newBuilder().build()).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), getAuthHeaderValue(getBtaDia().getSsn()))).
        when().
                put("/api/units").
        then().
                statusCode(HttpStatus.FORBIDDEN.value());

    }

    @Test
    public void test02SaveWithValidationViolations() {
        Unit unit = UnitBuilder.newBuilder()
                .country(null)
                .requestor(null)
                .commonName(null)
                .postCode(null)
                .city(null)
                .streetAndHouseNo(null)
                .companyName(null)
                .phoneNumber(VERY_LONG_STRING)
                .eMail(VERY_LONG_STRING)
                .identifierType(null)
                .identifierValue(null)
                .build();

        authHeaderValue = getAuthHeaderValue(getFlowersAdmin().getSsn());

        given().
                body(unit).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                put("/api/units").
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", equalTo(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(12)).
                body("fieldErrors.find{it.code == 'invalid' && it.field =='phoneNumber'}.rejectedValue", equalTo(unit.getPhoneNumber())).
                body("fieldErrors.find{it.code == 'invalid' && it.field =='eMail'}.rejectedValue", equalTo(unit.geteMail())).
                body("fieldErrors.find{it.code == 'length' && it.field =='eMail'}.rejectedValue", equalTo(unit.geteMail())).
                body("fieldErrors.find{it.code == 'required' && it.field =='identifier.type'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='identifier.value'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='commonName'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='companyName'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='postCode'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='city'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='streetAndHouseNo'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='requestor'}.rejectedValue", nullValue()).
                body("fieldErrors.find{it.code == 'required' && it.field =='country'}.rejectedValue", nullValue());

        unit.setCompanyName(VERY_LONG_STRING);
        unit.setCommonName(VERY_LONG_STRING);
        unit.setPostCode(VERY_LONG_STRING);
        unit.setCity(VERY_LONG_STRING);
        unit.setStreetAndHouseNo(VERY_LONG_STRING);
        unit.getIdentifier().setType(CompanyIdentifier.Type.OTHER);
        unit.getIdentifier().setValue(VERY_LONG_STRING);

        given().
                body(unit).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                put("/api/units").
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.isEmpty()", equalTo(Boolean.TRUE)).
                body("fieldErrors.size()", equalTo(11)).
                body("fieldErrors.find{it.code == 'length' && it.field == 'companyName'}.rejectedValue", equalTo(unit.getCompanyName())).
                body("fieldErrors.find{it.code == 'length' && it.field == 'commonName'}.rejectedValue", equalTo(unit.getCommonName())).
                body("fieldErrors.find{it.code == 'length' && it.field == 'postCode'}.rejectedValue", equalTo(unit.getPostCode())).
                body("fieldErrors.find{it.code == 'length' && it.field == 'city'}.rejectedValue", equalTo(unit.getCity())).
                body("fieldErrors.find{it.code == 'length' && it.field == 'streetAndHouseNo'}.rejectedValue", equalTo(unit.getStreetAndHouseNo())).
                body("fieldErrors.find{it.code == 'length' && it.field == 'identifier.value'}.rejectedValue", equalTo(unit.getIdentifier().getValue()));
    }

    @Test
    public void test03SaveUnit() {
        Unit unit = UnitBuilder.newBuilder().country(getTestCoutry()).requestor(getDgSante()).build();

        unitId = given().
                body(unit).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                put("/api/units").
        then().
                statusCode(HttpStatus.OK.value()).
        extract().
                jsonPath().getLong("id");
    }

    @Test
    public void test04FindAllUnits() {
        Long unitsCount = unitRepository.count();

        given().
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                get("/api/units").
        then().
                statusCode(HttpStatus.OK.value()).
                body("data.size()", equalTo(unitsCount.intValue())).
                body("totalCount", equalTo(unitsCount.intValue())).
                body("data.find{it.id == " + unitId.intValue() + "}.id", equalTo(unitId.intValue()));
    }

    @Test
    public void test05IsValuableDataModifiable() {
        given().
                header(new Header(getAuthHeaderName(), authHeaderValue)).
                when().
                get("/api/units/" + unitId.intValue() + "/valuable_data_modifiable").
        then().
                statusCode(HttpStatus.OK.value()).
                body(equalTo(Boolean.TRUE.toString()));
    }

    @Test
    public void test06CreateDuplicate() {
        Unit unit = UnitBuilder.newBuilder().country(getTestCoutry()).requestor(getDgSante()).build();

        given().
                body(unit).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                put("/api/units").
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.size()", equalTo(1)).
                body("globalErrors[0].code", equalTo("duplicate"));
    }

    @Test
    public void test07UpdateValuableFieldsWhenItIsPossible() {
        Unit unit = entityManager.find(Unit.class, unitId);
        unit.setCommonName("New Common name");

        given().
                body(unit).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                post("/api/units/" + unitId).
        then().
                statusCode(HttpStatus.OK.value());

        getDgsanteDia().setUnit(entityManager.find(Unit.class, unitId));
    }

    @Test
    public void test08UpdateValuableFieldsWhenItIsNotPossible() {
        Unit unit = UnitBuilder.copy(entityManager.find(Unit.class, unitId));
        unit.setCommonName("New Common name new");

        given().
                body(unit).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
         when().
                post("/api/units/" + unitId).
         then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.size()", equalTo(1)).
                body("globalErrors[0].code", equalTo("unit.has.related.data"));
    }

    @Test
    public void test09DeleteUnitWhenItIsNotPossible() {
        given().
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                delete("/api/units/" + unitId).
        then().
                statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).
                body("globalErrors.size()", equalTo(1)).
                body("globalErrors[0].code", equalTo("unit.has.related.data"));
    }

    @Test
    public void test10UpdateOptionalFields() {
        Unit unit = UnitBuilder.copy(entityManager.find(Unit.class, unitId));
        unit.setPhoneNumber("+33333333333");
        unit.seteMail("new.email@gmail.com");
        unit.setPostCode("8727272727");
        unit.setCity("Krakov");
        unit.setStreetAndHouseNo("Vasylkivska 10");

        given().
                body(unit).
                contentType(ContentType.JSON).
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                post("/api/units/" + unitId).
        then().
                statusCode(HttpStatus.OK.value());

        getDgsanteDia().setUnit(null);
    }

    @Test
    public void test11DeleteUnit() {
        given().
                header(new Header(getAuthHeaderName(), authHeaderValue)).
        when().
                delete("/api/units/" + unitId).
        then().
                statusCode(HttpStatus.OK.value());
    }

    @Test
    public void test12UnitWasDeleted() {
        assertThat(entityManager.find(Unit.class, unitId)).isNull();
    }
}
