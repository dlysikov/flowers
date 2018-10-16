package lu.luxtrust.flowers.entity;

import lu.luxtrust.flowers.entity.builder.*;
import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.enrollment.*;
import lu.luxtrust.flowers.enums.CertificateLevel;
import lu.luxtrust.flowers.enums.CertificateType;
import lu.luxtrust.flowers.enums.Device;
import org.junit.Test;

import javax.validation.groups.Default;

import java.math.BigInteger;

import static lu.luxtrust.flowers.validation.ValidationGroups.*;
import static org.assertj.core.api.Assertions.assertThat;


public class CertificateOrderValidationTest extends AbstractEntityValidationTest<CertificateOrder> {

    private static final String TOKEN_SERIAL_NUMBER = "27-3686864-0";


    @Test
    public void emptyOrderValid() {
        assertThat(validator.validate(new CertificateOrder())).isEmpty();
    }

    @Test
    public void filledOrderValid() {
        CertificateOrder order = new CertificateOrder();
        order.setDevice(Device.MOBILE);
        order.setTokenSerialNumber(TOKEN_SERIAL_NUMBER);
        order.setHolder(HolderBuilder.newBuilder().build());
        order.setAddress(AddressBuilder.newBuilder().build());

        assertThat(validator.validate(order)).isEmpty();
    }

    @Test
    public void filledTokenInValidTokenSerialNumberForSentTOLRS() {
        CertificateOrder order = buildForProCertificate();
        order.setDevice(Device.TOKEN);
        order.setTokenSerialNumber("");
        assertValidationResult(order, "tokenSerialNumber", OrderSentToLRS.class);
    }

    @Test
    public void filledQCPCertLevelInValidAcceptedGTCForUserDraft() {
        CertificateOrder order = buildForProCertificate();
        order.getHolder().setCertificateLevel(CertificateLevel.QCP);
        order.setAcceptedGTC(Boolean.TRUE);
        assertValidationResult(order, "acceptedGTC", OrderUserDraft.class);
    }

    @Test
    public void filledNotQCPCertLevelInValidAcceptedGTCForUserDraft() {
        CertificateOrder order = buildForProCertificate();
        order.getHolder().setCertificateLevel(CertificateLevel.LCP);
        order.setAcceptedGTC(Boolean.FALSE);
        order.setUnit(UnitBuilder.newBuilder().build());
        assertValidationResult(order, "acceptedGTC", OrderUserDraft.class);
    }

    private CertificateOrder buildForProCertificate() {
        CertificateOrder order = new CertificateOrder();
        order.setAcceptedGTC(Boolean.TRUE);
        order.setDevice(Device.MOBILE);
        order.setTokenSerialNumber(TOKEN_SERIAL_NUMBER);
        Holder holder = HolderBuilder.newBuilder()
                .certificateType(CertificateType.PROFESSIONAL_PERSON)
                .nationality(NationalityBuilder.newBuilder().build())
                .build();
        order.setHolder(holder);
        order.setUnit(UnitBuilder.newBuilder().build());
        order.setDepartment("DEPARTMENT");
        order.setAddress(AddressBuilder.newBuilder().country(CountryBuilder.newBuilder().build()).build());
        return order;
    }

    @Test
    public void filledTokenSerialNumberInvalid() {
        CertificateOrder order = new CertificateOrder();
        order.setDevice(Device.MOBILE);
        order.setTokenSerialNumber("sssss1111111111111111111111111111");
        order.setHolder(HolderBuilder.newBuilder().certificateType(CertificateType.PRIVATE).build());
        order.setAddress(AddressBuilder.newBuilder().build());

        assertValidationResult(order, "tokenSerialNumber", Default.class);
    }

    @Test
    public void emptyDeviceInvalidForUserDraft() {
        CertificateOrder order = buildForProCertificate();
        order.setDevice(null);
        assertValidationResult(order, "device", OrderUserDraft.class);
    }

    @Test
    public void emptyUnitInvalidForUserDraft() {
        CertificateOrder order = buildForProCertificate();
        order.setUnit(null);
        assertValidationResult(order, "unit", OrderUserDraft.class);
    }

    @Test
    public void emptyExternalUserIdForExternalAPI() {
        CertificateOrder order = new CertificateOrder();
        order.setDevice(Device.MOBILE);
        order.setHolder(HolderBuilder.newBuilder().firstName("john").surName("smith").notifyEmail("aaa@gmail.com").build());
        order.setUnit(UnitBuilder.newBuilder().build());
        assertValidationResult(order, "userExternalId", OrderUserDraftAPI.class);
    }

    @Test
    public void emptyHolderFirstNameForExternalAPI() {
        CertificateOrder order = new CertificateOrder();
        order.setUnit(UnitBuilder.newBuilder().build());
        order.setDevice(Device.MOBILE);
        order.setUserExternalId("asdfasdfas");
        order.setHolder(HolderBuilder.newBuilder().firstName(null).surName("smith").notifyEmail("aaa@gmail.com").build());

        assertValidationResult(order, "holder.firstName", OrderUserDraftAPI.class);
    }

    @Test
    public void emptyHolderSurnameForExternalAPI() {
        CertificateOrder order = new CertificateOrder();
        order.setUserExternalId("asdfasdfas");
        order.setDevice(Device.MOBILE);
        order.setUnit(UnitBuilder.newBuilder().build());
        order.setHolder(HolderBuilder.newBuilder().firstName("JOhn").surName(null).notifyEmail("aaa@gmail.com").build());

        assertValidationResult(order, "holder.surName", OrderUserDraftAPI.class);
    }

    @Test
    public void emptyHolderNotifyEmailExternalAPI() {
        CertificateOrder order = new CertificateOrder();
        order.setUserExternalId("asdfasdfas");
        order.setUnit(UnitBuilder.newBuilder().build());
        order.setDevice(Device.MOBILE);
        order.setHolder(HolderBuilder.newBuilder().firstName("JOhn").surName("Smith").notifyEmail(null).build());

        assertValidationResult(order, "holder.notifyEmail", OrderUserDraftAPI.class);
    }

    @Test
    public void nullHolderlExternalAPI() {
        CertificateOrder order = new CertificateOrder();
        order.setUnit(UnitBuilder.newBuilder().build());
        order.setUserExternalId("asdfasdfas");
        order.setHolder(null);
        order.setDevice(Device.MOBILE);

        assertValidationResult(order, "holder", OrderUserDraftAPI.class);
    }

    @Test
    public void nullUnitExternalAPI() {
        CertificateOrder order = new CertificateOrder();
        order.setUnit(null);
        order.setDevice(Device.MOBILE);
        order.setUserExternalId("asdfasdfas");
        order.setHolder(HolderBuilder.newBuilder().build());

        assertValidationResult(order, "unit", OrderUserDraftAPI.class);
    }

    @Test
    public void emptyUnitRequestorExternalAPI() {
        CertificateOrder order = new CertificateOrder();
        order.setUnit(UnitBuilder.newBuilder().requestor(null).build());
        order.setUserExternalId("asdfasdfas");
        order.setHolder(HolderBuilder.newBuilder().build());
        order.setDevice(Device.MOBILE);

        assertThat(validator.validate(order)).isEmpty();
    }

    @Test
    public void emptyAddressWithTokenUserDraft() {
        CertificateOrder order = buildForProCertificate();
        order.setDevice(Device.TOKEN);
        order.setAddress(null);

        assertValidationResult(order, "address", OrderUserDraft.class);
    }
}