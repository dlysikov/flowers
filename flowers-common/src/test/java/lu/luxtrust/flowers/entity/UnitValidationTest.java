package lu.luxtrust.flowers.entity;

import lu.luxtrust.flowers.entity.builder.UnitBuilder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import org.junit.Test;

import javax.persistence.Temporal;
import javax.validation.groups.Default;

public class UnitValidationTest extends AbstractEntityValidationTest<Unit>{

    @Test
    public void emptyCountryIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().country(null).build();
        assertValidationResult(unit, "country", Default.class);
    }

    @Test
    public void emptyRequestorIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().requestor(null).build();
        assertValidationResult(unit, "requestor", Default.class);
    }

    @Test
    public void emptyPostCodeIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().postCode(null).build();
        assertValidationResult(unit, "postCode", Default.class);
    }

    @Test
    public void toLongPostCodeIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().postCode("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").build();
        assertValidationResult(unit, "postCode", Default.class);
    }

    @Test
    public void emptyCityIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().city(null).build();
        assertValidationResult(unit, "city", Default.class);
    }

    @Test
    public void toLongCityIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().city("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").build();
        assertValidationResult(unit, "city", Default.class);
    }

    @Test
    public void emptyStreetAndHouseNoIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().streetAndHouseNo(null).build();
        assertValidationResult(unit, "streetAndHouseNo", Default.class);
    }

    @Test
    public void toLongStreetAndHouseNoIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().streetAndHouseNo("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").build();
        assertValidationResult(unit, "streetAndHouseNo", Default.class);
    }

    @Test
    public void emptyCompanyNameIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().companyName(null).build();
        assertValidationResult(unit, "companyName", Default.class);
    }

    @Test
    public void toLongCompanyNameIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().companyName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").build();
        assertValidationResult(unit, "companyName", Default.class);
    }

    @Test
    public void emptyCommonNameIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().commonName(null).build();
        assertValidationResult(unit, "commonName", Default.class);
    }

    @Test
    public void toLongCommonNameIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().commonName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").build();
        assertValidationResult(unit, "commonName", Default.class);
    }

    @Test
    public void toLongPhoneNumberIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().phoneNumber("+3803453453453453453453453453453453534534").build();
        assertValidationResult(unit, "phoneNumber", Default.class);
    }

    @Test
    public void toShortPhoneNumberIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().phoneNumber("+380").build();
        assertValidationResult(unit, "phoneNumber", Default.class);
    }

    @Test
    public void wrongPhoneNumberIsInvalid(){
        Unit unit = UnitBuilder.newBuilder().phoneNumber("+380A").build();
        assertValidationResult(unit, "phoneNumber", Default.class);
    }

    @Test
    public void toLongEmailIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().eMail("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA@AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA.com").build();
        assertValidationResult(unit, "eMail", Default.class);
    }

    @Test
    public void wrongEmailIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().eMail("aaaaa").build();
        assertValidationResult(unit, "eMail", Default.class);
    }

    @Test
    public void emptyCompanyIdentifierTypeIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().identifierType(null).build();
        assertValidationResult(unit, "identifier.type", Default.class);
    }

    @Test
    public void emptyCompanyIdentifierValueIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().identifierValue(null).build();
        assertValidationResult(unit, "identifier.value", Default.class);
    }

    @Test
    public void toLongCompanyIdentifierValusIsInvalid() {
        Unit unit = UnitBuilder.newBuilder().identifierValue("dddddddddddddddddddddddddddddddddddd").build();
        assertValidationResult(unit,"identifier.value", Default.class);
    }
}
