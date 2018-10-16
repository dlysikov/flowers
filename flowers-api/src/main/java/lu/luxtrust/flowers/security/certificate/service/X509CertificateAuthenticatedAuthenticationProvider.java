package lu.luxtrust.flowers.security.certificate.service;

import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.repository.RequestorRepository;
import lu.luxtrust.flowers.security.RequestorRestAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import java.security.cert.X509Certificate;


public class X509CertificateAuthenticatedAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(X509CertificateAuthenticatedAuthenticationProvider.class);

    private final RequestorRepository RequestorRepository;

    public X509CertificateAuthenticatedAuthenticationProvider(RequestorRepository RequestorRepository) {
        this.RequestorRepository = RequestorRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication.getPrincipal() instanceof X509Certificate)) {
            return null;
        }
        X509Certificate certificate = (X509Certificate) authentication.getPrincipal();
        Requestor requestor = RequestorRepository.findRequestorByCsn(certificate.getSerialNumber().toString());
        if (requestor == null) {
           LOG.warn("Unknown requestor certificate {}", certificate.toString());
           throw new BadCredentialsException("Unknown certificate");
        }

        RequestorRestAuthenticationToken token = new RequestorRestAuthenticationToken(requestor);
        token.setAuthenticated(Boolean.TRUE);
        return token;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.equals(authentication);
    }
}
