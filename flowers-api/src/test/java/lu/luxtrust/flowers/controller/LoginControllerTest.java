package lu.luxtrust.flowers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.properties.OrelyAuthProperties;
import lu.luxtrust.flowers.repository.UserRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.security.jwt.JWTService;
import lu.luxtrust.flowers.security.orely.OrelyAuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

    private static final String SAML_REQUEST = "SDFSDFSDF";
    private static final String JWT = "JWT_T";
    private static final String REFRESH_JWT = "REFRESH_JWT_T";

    @Mock
    private OrelyAuthenticationService orelyAuthenticationService;
    @Mock
    private OrelyAuthProperties orelyAuthProperties;
    @Mock
    private JWTService jwtService;
    @Mock
    private HttpServletResponse response;
    @Mock
    private UserRepository userRepository;
    private RestAuthenticationToken authencation;
    private URL destURL;

    private ObjectMapper objectMapper = new ObjectMapper();
    private LoginController target;

    @Before
    public void init() throws Exception {
        when(jwtService.getToken(response)).thenReturn(JWT);
        when(jwtService.getRefreshToken(response)).thenReturn(REFRESH_JWT);
        Unit uu = new Unit();
        this.authencation = new RestAuthenticationToken("1111", 1l, Collections.singletonList(new SimpleGrantedAuthority("CSD_AGENT")), uu);
        this.destURL = new URL("http", "dsfsdf", 111, "sddd");
        when(orelyAuthProperties.getDestinationUrl()).thenReturn(destURL);
        when(orelyAuthenticationService.generateSAMLRequest(any(String.class))).thenReturn(SAML_REQUEST);
        this.target = new LoginController(orelyAuthenticationService, orelyAuthProperties,  objectMapper, userRepository, jwtService);
    }

    @Test
    public void loginWithLanguage() throws Exception {
        ModelAndView mv = target.login("EN");
        assertThat(mv.getModel().get("samlRequest")).isEqualTo(SAML_REQUEST);
        assertThat(mv.getModel().get("destination")).isSameAs(destURL);
        assertThat(mv.getViewName()).isSameAs("login");

        verify(orelyAuthProperties).getDestinationUrl();
        verify(orelyAuthenticationService).generateSAMLRequest("EN");
    }

    @Test
    public void loginSuccess() throws Exception {
        ModelAndView mv = target.loginSuccess(response, authencation);
        assertThat(mv.getViewName()).isEqualTo("login-success");
        assertThat(mv.getModel().get("jwt")).isEqualTo(JWT);
        assertThat(mv.getModel().get("refreshJwt")).isEqualTo(REFRESH_JWT);
        assertThat(mv.getModel().get("authentication")).isEqualTo(objectMapper.writeValueAsString(authencation));
    }
}