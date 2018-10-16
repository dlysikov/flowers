package lu.luxtrust.flowers.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AuthenticationException authException;

    @Test
    public void commence() throws IOException {
        RestAuthenticationEntryPoint target = new RestAuthenticationEntryPoint();
        target.commence(request, response, authException);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}