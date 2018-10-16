package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.Device;
import lu.luxtrust.flowers.validation.annotation.TokenSerialNumberFilled;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidatorContext;

public class TokenSerialNumberValidator extends AbstractConstraintValidator<TokenSerialNumberFilled, CertificateOrder> {
    private static final String MSG = "org.hibernate.validator.constraints.NotEmpty.message";

    @Override
    public void initialize(TokenSerialNumberFilled tokenSerialNumberFilled) {
    }

    @Override
    public boolean isValid(CertificateOrder order, ConstraintValidatorContext ctx) {
        boolean valid = Boolean.TRUE;
        if (order.getDevice() != null && order.getDevice() == Device.TOKEN) {
            if (StringUtils.isEmpty(order.getTokenSerialNumber())) {
                addConstraintViolation("tokenSerialNumber", ctx, MSG);
                valid = Boolean.FALSE;
            }
        }
        return valid;
    }
}
