package lu.luxtrust.flowers.validation.validator;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;

public class AdultValidatorTest {

    private AdultValidator validator = new AdultValidator();

    @Test
    public void isValidWhenMore18() {
        Calendar bday = new GregorianCalendar();
        bday.add(Calendar.DAY_OF_YEAR, -1);
        bday.add(Calendar.YEAR, -18);
        assertThat(validator.isValid(bday.getTime(), null)).isTrue();
    }

    @Test
    public void isInvalidWhenLess18() {
        Calendar bday = new GregorianCalendar();
        bday.add(Calendar.DAY_OF_YEAR, 1);
        bday.add(Calendar.YEAR, -18);
        assertThat(validator.isValid(bday.getTime(), null)).isFalse();
    }

    @Test
    public void isValidWhenNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }
}