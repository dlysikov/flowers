package lu.luxtrust.flowers.model;

import lu.luxtrust.flowers.entity.AbstractEntityValidationTest;
import lu.luxtrust.flowers.entity.enrollment.Holder;
import org.junit.Test;

import javax.validation.groups.Default;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OrderUserPageMobileCodeVerificationValidationTest extends AbstractEntityValidationTest<OrderUserPageMobileCodeVerification> {

    @Test
    public void emptyPageHashInvalid() {
        OrderUserPageMobileCodeVerification v = new OrderUserPageMobileCodeVerification();
        v.setMobileCode("1111111");

        assertValidationResult(v, "pageHash", Default.class);
    }

    @Test
    public void emptyMobileCodeInvalid() {
        OrderUserPageMobileCodeVerification v = new OrderUserPageMobileCodeVerification();
        v.setPageHash("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        assertValidationResult(v, "mobileCode", Default.class);
    }

    @Test
    public void toSmallMobileCodeInvalid() {
        OrderUserPageMobileCodeVerification v = new OrderUserPageMobileCodeVerification();
        v.setMobileCode("aaaaaa");
        v.setPageHash("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        assertValidationResult(v, "mobileCode", Default.class);
    }

    @Test
    public void toBigMobileCodeInvalid() {
        OrderUserPageMobileCodeVerification v = new OrderUserPageMobileCodeVerification();
        v.setMobileCode("aaaaaaaaa");
        v.setPageHash("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        assertValidationResult(v, "mobileCode", Default.class);
    }

    @Test
    public void toSmallPageHashInvalid() {
        OrderUserPageMobileCodeVerification v = new OrderUserPageMobileCodeVerification();
        v.setPageHash("aaaaaa");
        v.setMobileCode("1111111");

        assertValidationResult(v, "pageHash", Default.class);
    }

    @Test
    public void toBigPageHashInvalid() {
        OrderUserPageMobileCodeVerification v = new OrderUserPageMobileCodeVerification();
        v.setPageHash("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        v.setMobileCode("1111111");

        assertValidationResult(v, "pageHash", Default.class);
    }
}