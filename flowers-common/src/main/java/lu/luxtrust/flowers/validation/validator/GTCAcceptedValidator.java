package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.Order;
import lu.luxtrust.flowers.enums.CertificateLevel;
import lu.luxtrust.flowers.validation.annotation.GTCAccepted;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidatorContext;

public class GTCAcceptedValidator extends AbstractConstraintValidator<GTCAccepted, Order> {

    private static final String MESSAGE_TEMPLATE = "javax.validation.constraints.Assert%s.message";

    @Override
    public void initialize(GTCAccepted gtcAccepted) {

    }

    @Override
    public boolean isValid(Order order, ConstraintValidatorContext ctx) {
        boolean valid = Boolean.TRUE;
        if (CertificateOrder.class.isAssignableFrom(order.getClass())) {
            CertificateOrder cOrder = (CertificateOrder) order;
            if (cOrder.getHolder() != null && cOrder.getHolder().getCertificateLevel() != null) {
                CertificateLevel level = cOrder.getHolder().getCertificateLevel();
                valid = (!order.getAcceptedGTC() && level == CertificateLevel.QCP) || (order.getAcceptedGTC() && level != CertificateLevel.QCP);
                if (!valid) {
                    Boolean isQCP = CertificateLevel.QCP != level;
                    String msg = String.format(MESSAGE_TEMPLATE, StringUtils.capitalize(isQCP.toString()));
                    addConstraintViolation("acceptedGTC", ctx, msg);
                }
            }

        } else {
            valid = order.getAcceptedGTC();
            if (!valid) {
                addConstraintViolation("acceptedGTC", ctx, String.format(MESSAGE_TEMPLATE, StringUtils.capitalize(Boolean.TRUE.toString())));
            }
        }
        return valid;
    }
}
