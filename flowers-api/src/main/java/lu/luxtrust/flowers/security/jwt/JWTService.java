package lu.luxtrust.flowers.security.jwt;

import lu.luxtrust.flowers.model.CertificateDetails;
import lu.luxtrust.flowers.security.RestAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JWTService {

    void addTokenToResponse(HttpServletResponse response, RestAuthenticationToken token);

    void addRefreshTokenToResponse(HttpServletResponse response, RestAuthenticationToken token);

    CertificateDetails retrieveUserCertificateDetailsFromToken(HttpServletRequest request);

    CertificateDetails retrieveUserCertificateDetailsFromRefreshToken(HttpServletRequest request);

    String getToken(HttpServletResponse response);

    String getRefreshToken(HttpServletResponse response);

    String getToken(HttpServletRequest request);

    String getRefreshToken(HttpServletRequest request);
}
