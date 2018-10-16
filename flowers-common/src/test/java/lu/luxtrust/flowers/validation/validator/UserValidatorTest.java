package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.entity.builder.NationalityBuilder;
import lu.luxtrust.flowers.entity.builder.RequestorBuilder;
import lu.luxtrust.flowers.entity.builder.UnitBuilder;
import lu.luxtrust.flowers.entity.builder.UserBuilder;
import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.RoleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserValidatorTest {

    private static final String TEMPLATE = "{org.hibernate.validator.constraints.NotNull.message}";

    private static final Role ADMIN = new Role();
    private static final Role DIA = new Role();
    private static final Role ESEAL_MANAGER = new Role();

    static {
        ADMIN.setRoleType(RoleType.FLOWERS_ADMIN);
        DIA.setRoleType(RoleType.DIA);
        ESEAL_MANAGER.setRoleType(RoleType.ESEAL_MANAGER);
    }

    @Mock private ConstraintValidatorContext ctx;
    @Mock private ConstraintValidatorContext.ConstraintViolationBuilder ctxBuilder;
    @Mock private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext ctxNodeCustomizer;

    private UserValidator target;
    private User user;

    @Before
    public void init() {
        when(ctx.buildConstraintViolationWithTemplate(anyString())).thenReturn(ctxBuilder);
        when(ctxBuilder.addPropertyNode(anyString())).thenReturn(ctxNodeCustomizer);
        target = new UserValidator();
        user = UserBuilder.newBuilder()
                .unit(UnitBuilder.newBuilder().build())
                .nationality(NationalityBuilder.newBuilder().build())
                .build();
    }

    @Test
    public void valid() {
        user.setRoles(Collections.singleton(ADMIN));

        assertThat(target.isValid(user, ctx)).isTrue();
        verify(ctx, never()).disableDefaultConstraintViolation();
        verify(ctxBuilder, never()).addPropertyNode(anyString());
        verify(ctxNodeCustomizer, never()).addConstraintViolation();
    }

    @Test
    public void invalidWhenNoRolesDefined() {
        user.setRoles(null);

        assertThat(target.isValid(user, ctx)).isFalse();

        user.setRoles(Collections.emptySet());

        assertThat(target.isValid(user, ctx)).isFalse();
        verify(ctx, times(2)).buildConstraintViolationWithTemplate(TEMPLATE);
        verify(ctx, times(2)).disableDefaultConstraintViolation();
        verify(ctxBuilder, times(2)).addPropertyNode("roles");
        verify(ctxNodeCustomizer, times(2)).addConstraintViolation();
    }

    @Test
    public void invalidWhenDiaWithoutUnit() {
        user.setRoles(Collections.singleton(DIA));
        user.setUnit(null);

        assertInvalid("unit");
    }

    @Test
    public void invalidWhenEsealManagerWithoutUnit() {
        user.setRoles(Collections.singleton(ESEAL_MANAGER));
        user.setUnit(null);

        assertInvalid("unit");
    }

    @Test
    public void invalidWhenEsealManagerWithoutBirthDate() {
        user.setRoles(Collections.singleton(ESEAL_MANAGER));
        user.setBirthDate(null);

        assertInvalid("birthDate");
    }

    @Test
    public void invalidWhenEsealManagerWithoutNationality() {
        user.setRoles(Collections.singleton(ESEAL_MANAGER));
        user.setNationality(null);

        assertInvalid("nationality");
    }

    @Test
    public void invalidWhenEsealManagerWithoutPhoneNumber() {
        user.setRoles(Collections.singleton(ESEAL_MANAGER));
        user.setPhoneNumber(null);

        assertInvalid("phoneNumber");
    }

    @Test
    public void invalidWhenDiaAndEsealManagerWithoutUnitThenOnlyOneViolation() {
        user.setRoles(new HashSet<>(Arrays.asList(DIA, ESEAL_MANAGER)));
        user.setUnit(null);

        assertInvalid("unit");
    }

    private void assertInvalid(String property) {
        assertThat(target.isValid(user, ctx)).isFalse();
        verify(ctx).buildConstraintViolationWithTemplate(TEMPLATE);
        verify(ctx).disableDefaultConstraintViolation();
        verify(ctxBuilder).addPropertyNode(property);
        verify(ctxNodeCustomizer).addConstraintViolation();
    }

}