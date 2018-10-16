package lu.luxtrust.flowers.security.method.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lu.luxtrust.flowers.entity.builder.RoleBuilder;
import lu.luxtrust.flowers.entity.builder.UserBuilder;
import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.repository.UserRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserIsManagerOfEvaluatorTest {

    private static final Long USER_ID = 1L;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private RestAuthenticationToken authentication;
    private List authorities = new ArrayList();
    private Set<Role> roles;
    private List<Role> rolesToManage;

    private UserIsManagerOfEvaluator target;

    @Before
    public void init() {
        this.target = new UserIsManagerOfEvaluator(userRepository, userService);
        this.roles = Sets.newHashSet(RoleBuilder.role(RoleType.FLOWERS_ADMIN), RoleBuilder.role(RoleType.ESEAL_MANAGER));
        this.rolesToManage = Lists.newArrayList(RoleBuilder.role(RoleType.FLOWERS_ADMIN), RoleBuilder.role(RoleType.ESEAL_MANAGER), RoleBuilder.role(RoleType.DIA));

        when(authentication.getAuthorities()).thenReturn(authorities);
        authorities.add(new SimpleGrantedAuthority(RoleType.FLOWERS_ADMIN.toString()));
        when(userService.managedBy(Lists.newArrayList(RoleType.FLOWERS_ADMIN))).thenReturn(rolesToManage);
        when(userRepository.findRolesByUserId(USER_ID)).thenReturn(roles);
    }

    @Test
    public void supportedType() {
        assertThat(target.supportedType()).isEqualTo(User.class);
    }

    @Test
    public void isManagerOfEmptyRoles() {
        assertThat(target.isManagerOf(authentication, UserBuilder.newBuilder().build())).isTrue();
        assertThat(target.isManagerOf(authentication, UserBuilder.newBuilder().roles(new HashSet<>()).build())).isTrue();
    }

    @Test
    public void isManagerOfByObject() {
        Set<Role> userRoles = Sets.newHashSet(RoleBuilder.role(RoleType.DIA), RoleBuilder.role(RoleType.FLOWERS_ADMIN));
        assertThat(target.isManagerOf(authentication, UserBuilder.newBuilder().roles(userRoles).build())).isTrue();
    }

    @Test
    public void isManagerOfById() {
        assertThat(target.isManagerOf(authentication, USER_ID)).isTrue();
    }
}