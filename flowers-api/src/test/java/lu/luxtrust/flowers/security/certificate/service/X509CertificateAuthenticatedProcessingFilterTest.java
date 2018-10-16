package lu.luxtrust.flowers.security.certificate.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.web.util.matcher.RequestMatcher;
import sun.security.x509.X509CertImpl;

import javax.servlet.http.HttpServletRequest;
import java.security.cert.CertificateFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class X509CertificateAuthenticatedProcessingFilterTest {

    private static final String HEADER_NAME = "SSL_CLIENT_CRT";
    private static final String CERTIFICATE = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDPTCCAiWgAwIBAgIEGXLleDANBgkqhkiG9w0BAQsFADBPMQswCQYDVQQGEwJM\n" +
            "VTEWMBQGA1UEChMNTHV4VHJ1c3QgUy5BLjEaMBgGA1UECxMRSW50ZXJuYWwgVXNl\n" +
            "IE9ubHkxDDAKBgNVBAMTAzEyMzAeFw0xODAyMjQwOTMyMDNaFw0xODA1MjUwOTMy\n" +
            "MDNaME8xCzAJBgNVBAYTAkxVMRYwFAYDVQQKEw1MdXhUcnVzdCBTLkEuMRowGAYD\n" +
            "VQQLExFJbnRlcm5hbCBVc2UgT25seTEMMAoGA1UEAxMDMTIzMIIBIjANBgkqhkiG\n" +
            "9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr6Ay9FM/GsTy14OI/XSCWMHSNHFS7F/YMtzz\n" +
            "Cj+3WpKKIK4gSELM5rk456hwfZZC6jpb0WmBYM0831RK6EhoU3MrnZZdCG4KDQeB\n" +
            "J/0Gz6DTSr7kZNtZZ7Fl9H3Juqh6wSB8i4RnyuezVT6oC0DXSSjOdwFT6eZLJZ/1\n" +
            "FsojzCJd6XkmugioCZYennBUF6vP/57cDdP/0tvP+t7zH+6y6B4x6Ft0areO5Nbc\n" +
            "7oxWiV+XypdshrLJBxvyCLqE/bjOqvK7U5wzQRU1d0KzX1TXhqiDvZZSNleXRiMN\n" +
            "RxC5ieSUaBheVKlO/BhseHn/gSK4UvFYCKnHhYYOx3BgACooYwIDAQABoyEwHzAd\n" +
            "BgNVHQ4EFgQUtos1mY9oc8jS2bSeF0xD/FMl9CMwDQYJKoZIhvcNAQELBQADggEB\n" +
            "AEEKFt+A27A9sFGaWTkPdTmlYQMTgOkxg+bEHqsUA01r+yqYN9nQxETcKaXbHmT3\n" +
            "4+NhM8ENomfuY6lm1UQd0YvK2xPIW0TgyGe3E76iP6NlXz8So4O77NxsQkA+QxC4\n" +
            "uDMTXlXdhFT9HSPHt5vu0Ga9ceJ4bVrx9u+dCIcsKkRkfTD1Uh1/Quyhfxf4gLsD\n" +
            "ajmWPFvk/FoBEDiGGDYFbhD0KYis53nzuIxd763V2q4/CJOMmcz47SgTx1h0VpDb\n" +
            "jGm9QLoPhZ+HMW1Fs+E9SAWDS3I40CrYOEVhuO1Th2P+QgUreUf2VPhIUkb4AB74\n" +
            "+oK8C7HWwHUc8/dLChiNyiA=\n" +
            "-----END CERTIFICATE-----\n";

    @Mock
    private HttpServletRequest request;
    @Mock
    private RequestMatcher requestMatcher;

    private X509CertificateAuthenticatedProcessingFilter target;

    @Before
    public void init() throws Exception {
        this.target = new X509CertificateAuthenticatedProcessingFilter(HEADER_NAME, CertificateFactory.getInstance("X509"), requestMatcher);
    }

    @Test
    public void getPreAuthenticatedCredentials() {
        assertThat(target.getPreAuthenticatedCredentials(request)).isNull();
    }

    @Test
    public void getPreAuthenticatedPrincipal_RequestURINotMatches() {
        when(requestMatcher.matches(request)).thenReturn(Boolean.FALSE);
        assertThat(target.getPreAuthenticatedPrincipal(request)).isNull();
        verify(request, never()).getHeader(anyString());
    }

    @Test
    public void getPreAuthenticatedPrincipal_EmptyCertHeader() {
        when(requestMatcher.matches(request)).thenReturn(Boolean.TRUE);
        when(request.getHeader(HEADER_NAME)).thenReturn("");
        assertThat(target.getPreAuthenticatedPrincipal(request)).isNull();
        verify(request).getHeader(HEADER_NAME);
    }

    @Test
    public void getPreAuthenticatedPrincipal_InvalidCert() throws Exception {
        when(requestMatcher.matches(request)).thenReturn(Boolean.TRUE);
        when(request.getHeader(HEADER_NAME)).thenReturn("SDFSDFSDFS");
        assertThat(target.getPreAuthenticatedPrincipal(request)).isNull();
        verify(request).getHeader(HEADER_NAME);
    }

    @Test
    public void getPreAuthenticatedPrincipal() {
        when(requestMatcher.matches(request)).thenReturn(Boolean.TRUE);
        when(request.getHeader(HEADER_NAME)).thenReturn(CERTIFICATE);
        Object result = target.getPreAuthenticatedPrincipal(request);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(X509CertImpl.class);
        verify(request).getHeader(HEADER_NAME);
    }
}