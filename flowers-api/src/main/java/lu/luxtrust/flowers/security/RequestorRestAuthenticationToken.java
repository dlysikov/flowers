package lu.luxtrust.flowers.security;

import lu.luxtrust.flowers.entity.system.Requestor;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.ArrayList;

public class RequestorRestAuthenticationToken extends AbstractAuthenticationToken {

    private final Requestor requestor;

    public RequestorRestAuthenticationToken(Requestor requestor) {
        super(new ArrayList<>());
        this.requestor = requestor;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Requestor getPrincipal() {
        return requestor;
    }
}
