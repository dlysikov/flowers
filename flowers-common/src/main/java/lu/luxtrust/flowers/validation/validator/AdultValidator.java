package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.validation.annotation.Adult;

import javax.validation.ConstraintValidatorContext;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AdultValidator extends AbstractConstraintValidator<Adult, Date> {

    @Override
    public void initialize(Adult adult) {

    }

    @Override
    public boolean isValid(Date date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return true;
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, 18);
        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.MINUTE);
        return calendar.getTime().before(new Date());
    }
}
