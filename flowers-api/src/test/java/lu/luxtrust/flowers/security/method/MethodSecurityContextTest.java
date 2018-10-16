package lu.luxtrust.flowers.security.method;

import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MethodSecurityContextTest {

    @Mock
    private IsManagerOfEvaluator<User, RestAuthenticationToken> evaluator;

    private MethodSecurityContext target;

    @Before
    public void init() {
         target = new MethodSecurityContext();
         target.setIsManagerOfEvaluators(Arrays.asList(evaluator));
    }

    @Test
    public void getIsManagerOfEvaluator() {
        when(evaluator.supportedType()).thenReturn(User.class);

        assertThat(target.getIsManagerOfEvaluator("User")).isSameAs(evaluator);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsManagerOfEvaluatorEmptyType() {
        target.getIsManagerOfEvaluator("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsManagerOfEvaluatorNullType() {
        target.getIsManagerOfEvaluator(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIsManagerOfEvaluatorUnknownType() {
        when(evaluator.supportedType()).thenReturn(User.class);
        target.getIsManagerOfEvaluator("CertificateOrder");
    }


}