package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.validation.annotation.UserMandatoryConditions;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class UserValidator extends AbstractConstraintValidator<UserMandatoryConditions, User> {

    private static final String NULL = "org.hibernate.validator.constraints.NotNull.message";

    @Override
    public void initialize(UserMandatoryConditions userMandatoryConditions) {

    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext ctx) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            addConstraintViolation("roles", ctx, NULL);
            return false;
        }
        valid = true;
        Set<String> constraints = new HashSet<>();
        for (Role role : user.getRoles()) {
            if (role.getRoleType() == RoleType.DIA) {
                if (user.getUnit() == null) {
                    constraints.add("unit");
                }
            }
            if (role.getRoleType() == RoleType.ESEAL_MANAGER) {
                if (user.getUnit() == null) {
                    constraints.add("unit");
                }
                if (user.getBirthDate() == null) {
                    constraints.add("birthDate");
                }
                if (user.getNationality() == null) {
                    constraints.add("nationality");
                }
                if (StringUtils.isEmpty(user.getPhoneNumber())) {
                    constraints.add("phoneNumber");
                }
            }
        }
        for (String constraint : constraints) {
            addConstraintViolation(constraint, ctx, NULL);
        }
        return valid;
    }
}
