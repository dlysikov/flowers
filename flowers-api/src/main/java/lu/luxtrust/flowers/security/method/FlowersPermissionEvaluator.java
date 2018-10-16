package lu.luxtrust.flowers.security.method;

import lu.luxtrust.flowers.enums.Permission;
import lu.luxtrust.flowers.enums.RoleType;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.thymeleaf.util.StringUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FlowersPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || (targetDomainObject == null) || !(permission instanceof String)) {
            return false;
        }
        String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();

        return hasPrivilege(authentication, targetType, permission.toString().toUpperCase());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ((authentication == null) || StringUtils.isEmpty(targetType) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(authentication, targetType.toUpperCase(), permission.toString().toUpperCase());
    }

    private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
        Set<String> permissions = new HashSet<>();
        auth.getAuthorities().forEach((r) -> {
            permissions.addAll(RoleType.valueOf(r.getAuthority()).getPermissions().stream().map(Permission::toString).collect(Collectors.toList()));
        });
        return permissions.contains(permission.toUpperCase() + "_" + targetType.toUpperCase());
    }
}
