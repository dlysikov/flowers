package lu.luxtrust.flowers.security;

import lu.luxtrust.flowers.entity.common.Language;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.enums.Permission;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.model.CertificateDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestAuthenticationToken extends AbstractAuthenticationToken {

    private final String ssn;
    private final Long id;
    private final Unit unit;
    private Language defaultLanguage;

    public RestAuthenticationToken(String ssn,
                                   Long id,
                                   List<GrantedAuthority> authorities,
                                   Unit unit) {
        super(authorities);
        this.ssn = ssn;
        this.id = id;
        this.unit = unit;
    }

    public Set<Permission> getPermissions() {
        Set<Permission> permissions = new HashSet<>();
        this.getAuthorities().forEach((r) -> permissions.addAll(RoleType.valueOf(r.getAuthority()).getPermissions()));
        return Collections.unmodifiableSet(permissions);
    }

    public Unit getUnit() {
        return unit;
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getSsn() {
        return ssn;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return ssn;
    }

    @Override
    public CertificateDetails getDetails() {
        return (CertificateDetails) super.getDetails();
    }
}
