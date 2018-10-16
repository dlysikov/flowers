package lu.luxtrust.flowers.security.orely.impl;

import lu.luxtrust.flowers.model.CertificateDetails;
import lu.luxtrust.flowers.properties.OrelyAuthProperties;
import lu.luxtrust.flowers.security.orely.OrelyAuthenticationService;
import lu.luxtrust.flowers.security.orely.OrelyTrustedCertificate;
import lu.luxtrust.orely.api.exceptions.RequestException;
import lu.luxtrust.orely.api.exceptions.ResponseException;
import lu.luxtrust.orely.api.service.RequestParameters;
import lu.luxtrust.orely.api.service.RequestService;
import lu.luxtrust.orely.api.service.ResponseService;
import lu.luxtrust.orely.api.service.SAMLResult;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OrelyAuthenticationServiceImpl implements OrelyAuthenticationService {

    private static final String USER_DETAILS_PATTERN = "^T=(.+),[\\s]?SERIALNUMBER=(.+),[\\s]?GIVENNAME=(.+),[\\s]?SURNAME=(.+?),[\\s]?(.+)$";
    private static final int CERT_TYPE_GROUP = 1;
    private static final int SSN_GROUP = 2;
    private static final int GIVENAME_GROUP = 3;
    private static final int SURNAME_GROUP = 4;

    private final Pattern userDetailsPattern;
    private final Set<OrelyTrustedCertificate> authorizedCertificates;
    private final OrelyAuthProperties orelyAuthProperties;
    private final RequestService requestService;
    private final ResponseService responseService;

    @Autowired
    public OrelyAuthenticationServiceImpl(@Qualifier("orelyTrustedCertificates") Set<OrelyTrustedCertificate> authorizedCertificates,
                                          OrelyAuthProperties orelyAuthProperties,
                                          ResponseService responseService,
                                          RequestService requestService) {
        this.authorizedCertificates = new HashSet<>(authorizedCertificates);
        this.orelyAuthProperties = orelyAuthProperties;
        this.requestService = requestService;
        this.responseService = responseService;
        this.userDetailsPattern = Pattern.compile(USER_DETAILS_PATTERN);
    }

    @Override
    public String generateSAMLRequest(String language) throws RequestException {
        Assert.notNull(language, "language is not provided");

        RequestParameters params = new RequestParameters();
        params.setQaaLevel(orelyAuthProperties.getQaaLevel());
        params.setCertificateRequest(orelyAuthProperties.getCertificateRequest());
        params.setTspType(orelyAuthProperties.getTspType());
        params.setTspId(orelyAuthProperties.getTspId());
        if (!StringUtils.isEmpty(language)) {
            params.setLang(language.toUpperCase());
        }

        return requestService.createRequest(params, Boolean.TRUE).getRequest();
    }

    @Override
    public SAMLResult parseResponse(String response) throws ResponseException {
        return responseService.parseResponse(response);
    }

    @Override
    public boolean isAllowedSignerCertificateUsed(SAMLResult samlResult) {
        if (samlResult == null || samlResult.getIdpCertificate() == null) {
            return Boolean.FALSE;
        }
        X509Certificate certificate = toCertificate(samlResult.getIdpCertificate());
        if (certificate == null) {
            return Boolean.FALSE;
        }
        return authorizedCertificates.contains(new OrelyTrustedCertificate(certificate.getIssuerDN().getName(), certificate.getSerialNumber()));
    }

    private X509Certificate toCertificate(String certificate) {
        byte[] derCertificate = Base64.decodeBase64(certificate);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(derCertificate)) {
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(bis);
        } catch (CertificateException | IOException e) {
            return null;
        }
    }

    @Override
    public CertificateDetails retrieveUserCertificateDetails(SAMLResult samlResult) {
        if (samlResult.getDistinguishedName() != null) {
            Matcher matcher = userDetailsPattern.matcher(samlResult.getDistinguishedName());
            if (matcher.matches()) {
                return CertificateDetails.newBuilder()
                        .certificateType(matcher.group(CERT_TYPE_GROUP))
                        .ssn(matcher.group(SSN_GROUP))
                        .givenName(matcher.group(GIVENAME_GROUP))
                        .surname(matcher.group(SURNAME_GROUP))
                        .build();
            }
        }
        return null;
    }
}
