package lu.luxtrust.flowers.security.method.impl;

import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.repository.UserRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.security.method.IsManagerOfEvaluator;
import lu.luxtrust.flowers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserIsManagerOfEvaluator implements IsManagerOfEvaluator<User, RestAuthenticationToken> {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserIsManagerOfEvaluator(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public boolean isManagerOf(RestAuthenticationToken authentication, Long id) {
        if (id == null) {
            return Boolean.FALSE;
        }
        return managesRoles(userRepository.findRolesByUserId(id), authentication);
    }

    @Override
    public boolean isManagerOf(RestAuthenticationToken authentication, User targetObject) {
        if (targetObject == null) {
            return Boolean.FALSE;
        }
        if (targetObject.getRoles() == null || targetObject.getRoles().isEmpty()) {
            return Boolean.TRUE;
        }
        return managesRoles(targetObject.getRoles(), authentication);
    }

    private boolean managesRoles(Set<Role> target, RestAuthenticationToken authentication) {
        return userService.managedBy(getRoles(authentication)).removeAll(target);
    }

    private List<RoleType> getRoles(RestAuthenticationToken authentication) {
        return authentication.getAuthorities().stream().map(au -> RoleType.valueOf(au.getAuthority())).collect(Collectors.toList());
    }

    @Override
    public Class<User> supportedType() {
        return User.class;
    }
}
