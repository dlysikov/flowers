package lu.luxtrust.flowers.security.certificate.service;

import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.repository.RequestorRepository;
import lu.luxtrust.flowers.security.RequestorRestAuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.math.BigInteger;
import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class X509CertificateAuthenticatedAuthenticationProviderTest {

    @Mock
    private RequestorRepository RequestorRepository;
    @Mock
    private Authentication authentication;
    @Mock
    private X509Certificate x509Certificate;
    @Mock
    private Requestor requestor;

    private BigInteger serialNumber;
    private X509CertificateAuthenticatedAuthenticationProvider target;

    @Before
    public void init() {
        this.serialNumber = BigInteger.valueOf(10L);
        this.target = new X509CertificateAuthenticatedAuthenticationProvider(RequestorRepository);
        when(x509Certificate.getSerialNumber()).thenReturn(serialNumber);
        when(authentication.getPrincipal()).thenReturn(x509Certificate);
    }

    @Test
    public void supportsClass() {
        assertThat(target.supports(PreAuthenticatedAuthenticationToken.class)).isTrue();
    }

    @Test
    public void notSupportsClass() {
        assertThat(target.supports(Authentication.class)).isFalse();
    }

    @Test
    public void authenticateNotByX509Certificate() {
        Integer i = 1;
        when(authentication.getPrincipal()).thenReturn(i);

        assertThat(target.authenticate(authentication)).isNull();
        verify(RequestorRepository, never()).findRequestorByCsn(any(String.class));
    }

    @Test(expected = BadCredentialsException.class)
    public void authenticateByUnknownCertificate() {
        assertThat(target.authenticate(authentication)).isNull();
    }

    @Test
    public void authenticateByKnownCertificate() {
        when(RequestorRepository.findRequestorByCsn(serialNumber.toString())).thenReturn(requestor);
        Authentication result = target.authenticate(authentication);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(RequestorRestAuthenticationToken.class);
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getPrincipal()).isSameAs(requestor);
    }

}