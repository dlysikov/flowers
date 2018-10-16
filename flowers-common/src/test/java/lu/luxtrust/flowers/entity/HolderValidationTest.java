package lu.luxtrust.flowers.entity;

import lu.luxtrust.flowers.entity.builder.HolderBuilder;
import lu.luxtrust.flowers.entity.builder.NationalityBuilder;
import lu.luxtrust.flowers.entity.builder.RequestorBuilder;
import lu.luxtrust.flowers.entity.enrollment.Holder;
import org.junit.Test;

import javax.validation.groups.Default;

import static lu.luxtrust.flowers.validation.ValidationGroups.OrderDraft;
import static lu.luxtrust.flowers.validation.ValidationGroups.OrderUserDraft;
import static org.assertj.core.api.Assertions.assertThat;

public class HolderValidationTest extends AbstractEntityValidationTest<Holder> {

    @Test
    public void emptyHolderValid() {
        assertThat(validator.validate(new Holder(), Default.class)).isEmpty();
    }

    @Test
    public void filledHolderValidBy() {
        Holder holder = HolderBuilder.newBuilder()
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertThat(validator.validate(holder, Default.class)).isEmpty();
    }

    @Test
    public void filledHolderFirstNameInvalid() {
        Holder holder = HolderBuilder.newBuilder()
                .firstName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .build();

        assertValidationResult(holder, "firstName", Default.class);
    }

    @Test
    public void emptyHolderFirstNameInvalidByDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .firstName("")
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "firstName", OrderDraft.class);
    }

    @Test
    public void filledHolderFirstNameInvalidByDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .firstName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "firstName", OrderDraft.class);
    }

    @Test
    public void filledHolderSurNameInvalid() {
        Holder holder = HolderBuilder.newBuilder()
                .surName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .build();

        assertValidationResult(holder, "surName", Default.class);
    }

    @Test
    public void emptyHolderSurNameInvalidByDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .surName("")
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "surName", OrderDraft.class);
    }

    @Test
    public void filledHolderSurNameInvalidByDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .surName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "surName", OrderDraft.class);
    }

    @Test
    public void emptyHolderNationalityInvalidByUserDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .build();

        assertValidationResult(holder, "nationality", OrderUserDraft.class);
    }

    @Test
    public void filledHolderToBigEmailInvalid() {
        Holder holder = HolderBuilder.newBuilder()
                .secondEmail("dd@gmail.com")
                .email("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.com")
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "eMail", Default.class);
    }

    @Test
    public void filledHolderEmailInvalid() {
        Holder holder = HolderBuilder.newBuilder()
                .email("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .build();

        assertValidationResult(holder, "eMail", Default.class);
    }

    @Test
    public void filledHolderToBig2dEmailInvalid() {
        Holder holder = HolderBuilder.newBuilder()
                .secondEmail("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.com")
                .build();

        assertValidationResult(holder, "eMailSecond", Default.class);
    }

    @Test
    public void filledHolder2dEmailInvalid() {
        Holder holder = HolderBuilder.newBuilder()
                .secondEmail("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .build();

        assertValidationResult(holder, "eMailSecond", Default.class);
    }

    @Test
    public void emptyHolderCertificateTypeInvalidByDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .certificateType(null)
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "certificateType", OrderDraft.class);
    }

    @Test
    public void emptyHolderRoleTypeInvalidByDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .roleType(null)
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "roleType", OrderDraft.class);
    }

    @Test
    public void emptyHolderBirthDateInvalidByDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .birthDate(null)
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "birthDate", OrderDraft.class);
    }

    @Test
    public void filledHolderToBigNotifyEmailInvalid() {
        Holder holder = HolderBuilder.newBuilder()
                .notifyEmail("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa.com")
                .build();

        assertValidationResult(holder, "notifyEmail", Default.class);
    }

    @Test
    public void filledHolderNotifyEmailInvalid() {
        Holder holder = HolderBuilder.newBuilder()
                .notifyEmail("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .build();

        assertValidationResult(holder, "notifyEmail", Default.class);
    }

    @Test
    public void emptyHolderNotifyEmailInvalidByDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .notifyEmail("")
                .build();

        assertValidationResult(holder, "notifyEmail", OrderDraft.class);
    }

    @Test
    public void filledHolderEmptyNotifyEmailInvalidByFinalGroup() {
        Holder holder = HolderBuilder.newBuilder()
                .notifyEmail("")
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "notifyEmail", OrderDraft.class);
    }

    @Test
    public void filledHolderActivationCodeInvalid() {
        Holder holder = HolderBuilder.newBuilder()
                .activationCode("ddddd22222222222222222222222222222222222ddddd")
                .build();

        assertValidationResult(holder, "activationCode", Default.class);
    }

    @Test
    public void filledHolderToBigActivationCodeInvalidByUserDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .activationCode("dddddddddd")
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "activationCode", OrderUserDraft.class);
    }

    @Test
    public void filledHolderPhoneNumberInvalid() {
        Holder holder = HolderBuilder.newBuilder()
                .phoneNumber("33333333333333333333333333333333333333")
                .build();

        assertValidationResult(holder, "phoneNumber", Default.class);
    }

    @Test
    public void filledHolderToBigPhoneNumberInvalidUserDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .phoneNumber("33333333333333333333333333333333333333")
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "phoneNumber", OrderUserDraft.class);
    }

    @Test
    public void filledHolderPhoneNumberEmptyUserDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .phoneNumber(null)
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "phoneNumber", OrderUserDraft.class);
    }

    @Test
    public void filledHolderToSmallPhoneNumberInvalidByUserDraft() {
        Holder holder = HolderBuilder.newBuilder()
                .phoneNumber("333")
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "phoneNumber", OrderUserDraft.class);
    }

    @Test
    public void filledHolderEmptyCertificateLevelInvalid() {
        Holder holder = HolderBuilder.newBuilder()
                .certificateLevel(null)
                .nationality(NationalityBuilder.newBuilder().build())
                .build();

        assertValidationResult(holder, "certificateLevel", OrderDraft.class);
    }
}