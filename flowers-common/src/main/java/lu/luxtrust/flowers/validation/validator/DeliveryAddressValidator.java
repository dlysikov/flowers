package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.entity.enrollment.Address;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.enums.Device;
import lu.luxtrust.flowers.validation.annotation.DeliveryAddressFilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.*;
import java.util.Set;

@Component
public class DeliveryAddressValidator extends AbstractConstraintValidator<DeliveryAddressFilled, CertificateOrder> {

    private static final String NULL = "org.hibernate.validator.constraints.NotNull.message";

    @Autowired
    private Validator validator;
    private DeliveryAddressFilled deliveryAddressFilled;

    @Override
    public void initialize(DeliveryAddressFilled deliveryAddressFilled) {
        this.deliveryAddressFilled = deliveryAddressFilled;
        if (validator == null) {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            this.validator = factory.getValidator();
        }
    }

    @Override
    public boolean isValid(CertificateOrder certificateOrder, ConstraintValidatorContext ctx) {
        if (certificateOrder.getDevice() != Device.MOBILE) {
            if (certificateOrder.getAddress() != null) {
                Set<ConstraintViolation<Address>> violations = validator.validate(certificateOrder.getAddress(), deliveryAddressFilled.groups());
                boolean valid = violations.isEmpty();
                for (ConstraintViolation<Address> violation : violations) {
                    ctx.disableDefaultConstraintViolation();
                    ctx.buildConstraintViolationWithTemplate(violation.getMessageTemplate())
                            .addPropertyNode(deliveryAddressFilled.fieldName() + "." +violation.getPropertyPath().toString())
                            .addConstraintViolation();
                }
                return valid;
            } else if (!deliveryAddressFilled.nullValid()){
                addConstraintViolation("address", ctx, NULL);
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
