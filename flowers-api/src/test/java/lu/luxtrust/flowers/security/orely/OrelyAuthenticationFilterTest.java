package lu.luxtrust.flowers.security.orely;

import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.model.CertificateDetails;
import lu.luxtrust.flowers.properties.OrelyAuthProperties;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.UserService;
import lu.luxtrust.orely.api.exceptions.ResponseException;
import lu.luxtrust.orely.api.service.ResponseStatus;
import lu.luxtrust.orely.api.service.SAMLResult;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RunWith(MockitoJUnitRunner.class)
public class OrelyAuthenticationFilterTest {

    private static final String SAML_RESPONSE = "SAMLRESPONSE";
    private static final String SSN = "111111";

    @Mock
    private OrelyAuthenticationService orelyAuthenticationService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private SAMLResult samlResult;
    @Mock
    private UserService userService;
    @Mock
    private OrelyAuthProperties orelyAuthProperties;

    private OrelyAuthenticationFilter target;
    private CertificateDetails details;

    @Before
    public void init() throws Exception {
        this.target = new OrelyAuthenticationFilter("/login", orelyAuthenticationService, orelyAuthProperties, userService);
        this.details = CertificateDetails.newBuilder().ssn(SSN).build();
        Mockito.when(orelyAuthenticationService.parseResponse(SAML_RESPONSE)).thenReturn(samlResult);
        Mockito.when(request.getParameter(OrelyAuthenticationFilter.SAML_RESPONSE_ATTR_NAME)).thenReturn(SAML_RESPONSE);
    }

    @Test(expected = OrelyAuthenticationException.class)
    public void authenticateResponseStatusNotOk() {
        Mockito.when(samlResult.getStatus()).thenReturn(ResponseStatus.REQUESTER_QAA_LEVEL_NOT_SUPPORTED);
        target.attemptAuthentication(request, response);
    }

    @Test(expected = OrelyAuthenticationException.class)
    public void authenticateSignCertificateIsNotAuthorized() {
        Mockito.when(orelyAuthenticationService.isAllowedSignerCertificateUsed(samlResult)).thenReturn(Boolean.FALSE);
        Mockito.when(samlResult.getStatus()).thenReturn(ResponseStatus.OK);
        target.attemptAuthentication(request, response);
    }

    @Test(expected = OrelyAuthenticationException.class)
    public void authenticateSAMLResponseCantBeParsed() throws ResponseException {
        Mockito.when(orelyAuthenticationService.parseResponse(SAML_RESPONSE)).thenThrow(new ResponseException());
        target.attemptAuthentication(request, response);
    }

    @Test
    public void authenticateSuccess() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getSsn()).thenReturn(SSN);
        Role role = new Role();
        role.setRoleType(RoleType.DIA);
        Mockito.when(user.getRoles()).thenReturn(Sets.newLinkedHashSet(role));
        Mockito.when(user.getFirstName()).thenReturn(details.getGivenName());
        Mockito.when(user.getSurName()).thenReturn(details.getSurname());
        Mockito.when(samlResult.getDistinguishedName()).thenReturn("T=Professional Person, SERIALNUMBER=12312312312");
        Mockito.when(orelyAuthenticationService.isAllowedSignerCertificateUsed(samlResult)).thenReturn(Boolean.TRUE);
        Mockito.when(samlResult.getStatus()).thenReturn(ResponseStatus.OK);
        Mockito.when(orelyAuthenticationService.retrieveUserCertificateDetails(samlResult)).thenReturn(details);
        Mockito.when(userService.findActiveBySSN(SSN)).thenReturn(user);

        Authentication authentication = target.attemptAuthentication(request, response);

        Assertions.assertThat(authentication)
                .isInstanceOf(RestAuthenticationToken.class)
                .hasFieldOrPropertyWithValue("ssn", SSN)
                .hasFieldOrPropertyWithValue("details", details)
                .hasFieldOrPropertyWithValue("authenticated", Boolean.TRUE);
    }

    @Test(expected = OrelyAuthenticationException.class)
    public void authenticateWithoutCertificateDetails() {
        Mockito.when(orelyAuthenticationService.isAllowedSignerCertificateUsed(samlResult)).thenReturn(Boolean.TRUE);
        Mockito.when(samlResult.getStatus()).thenReturn(ResponseStatus.OK);
        target.attemptAuthentication(request, response);
    }

    @Test(expected = OrelyAuthenticationException.class)
    public void authenticateUnknownCertificate() {
        Mockito.when(orelyAuthenticationService.isAllowedSignerCertificateUsed(samlResult)).thenReturn(Boolean.TRUE);
        Mockito.when(samlResult.getStatus()).thenReturn(ResponseStatus.OK);
        Mockito.when(orelyAuthenticationService.retrieveUserCertificateDetails(samlResult)).thenReturn(details);
        target.attemptAuthentication(request, response);
    }
}