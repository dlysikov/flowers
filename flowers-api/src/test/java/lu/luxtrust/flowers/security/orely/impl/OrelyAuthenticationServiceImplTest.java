package lu.luxtrust.flowers.security.orely.impl;

import lu.luxtrust.flowers.model.CertificateDetails;
import lu.luxtrust.flowers.properties.OrelyAuthProperties;
import lu.luxtrust.flowers.security.orely.OrelyTrustedCertificate;
import lu.luxtrust.orely.api.exceptions.RequestException;
import lu.luxtrust.orely.api.exceptions.ResponseException;
import lu.luxtrust.orely.api.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class OrelyAuthenticationServiceImplTest {

    private static final String CERT = "MIIGVDCCBDygAwIBAgIUJoYCIsGeO1DMI8iSP2Lid2VwVmMwDQYJKoZIhvcNAQELBQAwQTELMAkG\n" +
            "A1UEBhMCTFUxFjAUBgNVBAoMDUx1eFRydXN0IFMuQS4xGjAYBgNVBAMMEUx1eFRydXN0IFNTTCBD\n" +
            "QSA1MB4XDTE3MDIxMDA4MDkxN1oXDTIwMDIxMDA4MDkxN1owWzELMAkGA1UEBhMCTFUxETAPBgNV\n" +
            "BAcTCENhcGVsbGVuMRYwFAYDVQQKEw1MdXhUcnVzdCBTLkEuMSEwHwYDVQQDExhPcmVseSBTZXJ2\n" +
            "ZXIgSW50ZWdyYXRpb24wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC/2sRmf0Ip75DJ\n" +
            "SJwLd2u2DArfGu4zqMnrGecnV2vQHGLCyKmg6u20bzDNWBuPDKSQfzTPMsKQXF9GYoyraBY8GgUd\n" +
            "oLbupJrQaUlEsyK52p9jvCJXqJzDdtpTXk6QHmov/5r/YkAOr3FLQI2pwwUAA51JV4OpZM8L61qJ\n" +
            "Ua1S82w/u4dbCKXnOObPsxots9LwEv3Ycznl+nftg7m7ThBolzdCTLSO8AWavuzKeK369BFCtYZR\n" +
            "R79WkxwVvN/As344bafS2ehQljPkYCdmuZ8qkQYIpKqkE3+dCgfhV2M0y94gKSNl227rxfs5GI13\n" +
            "o80NCXljf2d68+BjWaOFrkutAgMBAAGjggIoMIICJDBnBggrBgEFBQcBAQRbMFkwJwYIKwYBBQUH\n" +
            "MAGGG2h0dHA6Ly9zc2wub2NzcC5sdXh0cnVzdC5sdTAuBggrBgEFBQcwAoYiaHR0cDovL2NhLmx1\n" +
            "eHRydXN0Lmx1L0xUU1NMQ0E1LmNydDCCAR4GA1UdIASCARUwggERMIIBAwYIK4ErAQEKBQUwgfYw\n" +
            "gccGCCsGAQUFBwICMIG6GoG3THV4VHJ1c3QgU3RhbmRhcmQgU1NML1RMUyBDbGllbnQgQXV0aGVu\n" +
            "dGljYXRpb24gQ2VydGlmaWNhdGUuIE5vdCBTdXBwb3J0ZWQgYnkgU1NDRCwgS2V5IEdlbmVyYXRp\n" +
            "b24gYnkgU3Vic2NyaWJlci4gR1RDLCBDUCBhbmQgQ1BTIG9uIGh0dHA6Ly9yZXBvc2l0b3J5Lmx1\n" +
            "eHRydXN0Lmx1LiBTaWduZWQgYnkgYSBTU0wgQ0EuMCoGCCsGAQUFBwIBFh5odHRwczovL3JlcG9z\n" +
            "aXRvcnkubHV4dHJ1c3QubHUwCAYGBACPegEDMA4GA1UdDwEB/wQEAwIEsDAdBgNVHSUEFjAUBggr\n" +
            "BgEFBQcDAgYIKwYBBQUHAwQwHwYDVR0jBBgwFoAUCZdFjhOsalxvH0FM9F7FwQzGbxkwNAYDVR0f\n" +
            "BC0wKzApoCegJYYjaHR0cDovL2NybC5sdXh0cnVzdC5sdS9MVFNTTENBNS5jcmwwEQYDVR0OBAoE\n" +
            "CEJTZ0gNAIKYMA0GCSqGSIb3DQEBCwUAA4ICAQCpj+FQbiKYB2jU0pzx1we7AtdyZ1au3A2C05zV\n" +
            "j2H7gueQ2Xm1t/bMndVaFT8gJNpTA9GlIXJGl3C63wBlmq+vtEvjy15VwHkfgENIARgJSUJYAX+k\n" +
            "JxGDKTRqKu31tT6jJK/wc9l20O4mBr5hLLk2bcNfTega/hvD+j267AU0PGCw2uTPeGdr4TKcakeU\n" +
            "/Tkznp/PYEe5sKQlCsY7GqAHwtJ51Kd2lY1k/PfpG1HDYitKUUx1NpHfyIY9NytUZMiR+Ejea7SV\n" +
            "588puZNhleHcGYu1SWAjkOYvxTxrlhHFBQFkZQi0hJb6Zgy1Vvy5GBqGUchdlXQTQDNJfgXgJ2Yq\n" +
            "jatGzdeuOPUlS/jHAMdYwng9lZPyEL4qup21N04mVm7wkE+U5JfhYBxMyRMxAokcu9zNZBkfofYy\n" +
            "ehOaMn1QwRqt1ylgFAPqZHOMiEZhCEkcZanjvChNw5UUSeYnsQGKhPNy9VKrY//dPmwI7myADF+C\n" +
            "Tjpd3W0hQNj7wP1dFdcON9IrxQ4Ok+hRn+nRaKp7U3sBRwgn+noWdedk2bammH18irJfsNyrIFYn\n" +
            "CagEFbkwVsFpkCRQVnH0gRDy184/nkikk+rkb0lZoUnl/++dZrax4uBd/QBSRlxjM87cuXItF+jT\n" +
            "8nSyIAuI4zSasrwEiuCFHz3xnH2TAJVJ7GGGYw==";

    @Mock
    private ResponseService responseService;
    @Mock
    private RequestService requestService;
    @Mock
    private SAMLRequest samlRequest;
    @Mock
    private SAMLResult samlResult;
    private OrelyAuthProperties authProperties;
    private OrelyAuthenticationServiceImpl target;
    private Set<OrelyTrustedCertificate> authorizedCertificates;

    @Before
    public void init() throws RequestException {
        this.authorizedCertificates = new HashSet<>();
        this.authorizedCertificates.add(new OrelyTrustedCertificate("CN=LuxTrust SSL CA 5, O=LuxTrust S.A., C=LU", new BigInteger("26860222C19E3B50CC23C8923F62E27765705663", 16)));
        this.authorizedCertificates.add(new OrelyTrustedCertificate("CN=LuxTrust SSL CA, O=LuxTrust S.A., C=LU", new BigInteger("444C0907BE5A3C533E752CB0F40AD57DFE4A904B", 16)));

        this.authProperties = new OrelyAuthProperties();
        this.authProperties.setQaaLevel(4);
        this.authProperties.setCertificateRequest(CertificateRequest.OPTIONAL);
        this.authProperties.setTspType("tspType");
        this.authProperties.setTspId("tspId");

        this.target = new OrelyAuthenticationServiceImpl(authorizedCertificates, authProperties, responseService, requestService);
        Mockito.when(requestService.createRequest(any(RequestParameters.class), anyBoolean())).thenReturn(samlRequest);
    }


    @Test
    public void parseResponse() throws ResponseException {
        String response = "response";
        this.target.parseResponse(response);
        Mockito.verify(responseService).parseResponse(response);
    }

    @Test
    public void generateRequest() throws RequestException {
        this.target.generateSAMLRequest("EN");

        ArgumentCaptor<RequestParameters> requestParamsCaptor = ArgumentCaptor.forClass(RequestParameters.class);
        ArgumentCaptor<Boolean> encodeToBas64Captor = ArgumentCaptor.forClass(Boolean.class);
        Mockito.verify(requestService).createRequest(requestParamsCaptor.capture(), encodeToBas64Captor.capture());

        assertThat(encodeToBas64Captor.getValue()).isTrue();

        RequestParameters expectedRequestParams = new RequestParameters();
        expectedRequestParams.setQaaLevel(authProperties.getQaaLevel());
        expectedRequestParams.setCertificateRequest(authProperties.getCertificateRequest());
        expectedRequestParams.setTspType(authProperties.getTspType());
        expectedRequestParams.setTspId(authProperties.getTspId());
        expectedRequestParams.setLang("EN");

        RequestParameters params = requestParamsCaptor.getValue();
        assertThat(params.getExtensionParameters()).isEqualTo(expectedRequestParams.getExtensionParameters());

        Mockito.verify(samlRequest).getRequest();
    }


    @Test
    public void isAllowedSignerCertificateUsedNullSAMLResult() {
        assertThat(this.target.isAllowedSignerCertificateUsed(null)).isFalse();
    }

    @Test
    public void isAllowedSignerCertificateUsedWrongCertificatString() {
        Mockito.when(samlResult.getIdpCertificate()).thenReturn("SDFSDFSDF");
        assertThat(this.target.isAllowedSignerCertificateUsed(samlResult)).isFalse();
        Mockito.verify(samlResult, times(2)).getIdpCertificate();
    }

    @Test
    public void isAllowedSignerCertificateUsed() throws Exception {
        Mockito.when(samlResult.getIdpCertificate()).thenReturn(CERT);
        assertThat(this.target.isAllowedSignerCertificateUsed(samlResult)).isTrue();
        Mockito.verify(samlResult, times(2)).getIdpCertificate();
    }

    @Test
    public void retrieveUserCertificateDetails() {
        String ssn = "12312312312";
        String certificateType = "Professional Person";
        String givenName = "JOHN";
        String surname = "SMITH";
        Mockito.when(samlResult.getDistinguishedName()).thenReturn("T=" + certificateType + ", SERIALNUMBER=" + ssn + ", GIVENNAME=" + givenName + ", SURNAME=" + surname + ", DDS=AD");

        CertificateDetails details = target.retrieveUserCertificateDetails(samlResult);
        assertThat(details)
                .hasFieldOrPropertyWithValue("ssn", ssn)
                .hasFieldOrPropertyWithValue("certificateType", certificateType)
                .hasFieldOrPropertyWithValue("givenName", givenName)
                .hasFieldOrPropertyWithValue("surname", surname);
    }
}