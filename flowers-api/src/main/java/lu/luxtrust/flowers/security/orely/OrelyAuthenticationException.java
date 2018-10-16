package lu.luxtrust.flowers.security.orely;

import lu.luxtrust.orely.api.service.ResponseStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

public class OrelyAuthenticationException extends BadCredentialsException {

    public OrelyAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public OrelyAuthenticationException(String msg, ResponseStatus responseStatus) {
        super(String.format("[%s] - [%s] - message [%s] -> [%s]",
                msg,
                responseStatus.getSamlStatusCode(),
                responseStatus.getSamlStatusMessage(),
                responseStatus.getSamlStatusDetail()));
    }
}
