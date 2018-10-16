package lu.luxtrust.flowers.security.orely;

import lu.luxtrust.flowers.model.CertificateDetails;
import lu.luxtrust.orely.api.exceptions.RequestException;
import lu.luxtrust.orely.api.exceptions.ResponseException;
import lu.luxtrust.orely.api.service.SAMLResult;

public interface OrelyAuthenticationService {

    String generateSAMLRequest(String language) throws RequestException;

    SAMLResult parseResponse(String response) throws ResponseException;

    boolean isAllowedSignerCertificateUsed(SAMLResult samlResult);

    CertificateDetails retrieveUserCertificateDetails(SAMLResult samlResult);
}
