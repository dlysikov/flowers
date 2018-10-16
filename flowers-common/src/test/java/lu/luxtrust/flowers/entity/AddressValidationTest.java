package lu.luxtrust.flowers.entity;

import lu.luxtrust.flowers.entity.builder.AddressBuilder;
import lu.luxtrust.flowers.entity.builder.CountryBuilder;
import lu.luxtrust.flowers.entity.enrollment.Address;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.Set;

import static lu.luxtrust.flowers.validation.ValidationGroups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class AddressValidationTest extends AbstractEntityValidationTest<Address> {

    @Test
    public void emptyAddressValidByBasicGroup() {
        assertThat(validator.validate(new Address(), Default.class)).isEmpty();
    }

    @Test
    public void filledAddressValid() {
        Address address = AddressBuilder.newBuilder().build();
        assertThat(validator.validate(address, Default.class)).isEmpty();
    }

    @Test
    public void filledAddressValidByUserDraftGroup() {
        Address address = AddressBuilder.newBuilder()
                .country(CountryBuilder.newBuilder().build())
                .build();
        assertThat(validator.validate(address, OrderUserDraft.class)).isEmpty();
    }

    @Test
    public void filledAddressValidByDraftGroup() {
        Address address = AddressBuilder.newBuilder().build();
        assertThat(validator.validate(address, OrderDraft.class)).isEmpty();
    }

    @Test
    public void filledAddressNameNotValid() {
        Address address = AddressBuilder.newBuilder()
                .name("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffffffffffff")
                .build();
        assertValidationResult(address, "name", Default.class);
    }

    @Test
    public void filledAddressCompanyNotValid() {
        Address address = AddressBuilder.newBuilder()
                .company("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffffffffffff")
                .build();
        assertValidationResult(address, "company", Default.class);
    }

    @Test
    public void filledAddressAddressLine2NotValid() {
        Address address = AddressBuilder.newBuilder()
                .addressLine2("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffffffffffff")
                .build();
        assertValidationResult(address, "addressLine2", Default.class);
    }

    @Test
    public void filledAddressStreetAndHouseNoNotValid() {
        Address address = AddressBuilder.newBuilder()
                .streetAndHouseNo("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffffffffffff")
                .build();
        assertValidationResult(address, "streetAndHouseNo", Default.class);
    }

    @Test
    public void emptyAddressStreetAndHouseNoNotValidByUserDraftGroup() {
        Address address = AddressBuilder.newBuilder()
                .streetAndHouseNo("")
                .country(CountryBuilder.newBuilder().build())
                .build();
        assertValidationResult(address, "streetAndHouseNo", OrderUserDraft.class);
    }

    @Test
    public void filledAddressPostCodeNotValid() {
        Address address = AddressBuilder.newBuilder()
                .postCode("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffffffffffff")
                .build();
        assertValidationResult(address, "postCode", Default.class);
    }

    @Test
    public void emptyAddressPostCodeNotValidByUserDraftGroup() {
        Address address = AddressBuilder.newBuilder()
                .postCode("")
                .country(CountryBuilder.newBuilder().build())
                .build();
        assertValidationResult(address, "postCode", OrderUserDraft.class);
    }

    @Test
    public void filledAddressCityNotValid() {
        Address address = AddressBuilder.newBuilder()
                .city("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddffffffffffffff")
                .build();
        assertValidationResult(address, "city", Default.class);
    }

    @Test
    public void emptyAddressCityNotValidByUserDraftGroup() {
        Address address = AddressBuilder.newBuilder()
                .city("")
                .country(CountryBuilder.newBuilder().build())
                .build();
        assertValidationResult(address, "city", OrderUserDraft.class);
    }

    @Test
    public void emptyAddressCountryNotValidByUserDraftGroup() {
        Address address = AddressBuilder.newBuilder().build();
        assertValidationResult(address, "country", OrderUserDraft.class);
    }
}