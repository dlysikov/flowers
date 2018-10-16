package lu.luxtrust.flowers.security.method;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.RoleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FlowersPermissionEvaluatorTest {

    private FlowersPermissionEvaluator target = new FlowersPermissionEvaluator();

    @Mock
    private Authentication authentication;
    private List authorities = new ArrayList<>();

    @Before
    public void init() {
        when(authentication.getAuthorities()).thenReturn(authorities);
    }

    @Test
    public void hasPermissionNullAuthentication(){
        assertThat(target.hasPermission(null, new User(), "read")).isFalse();
        assertThat(target.hasPermission(null, 1L, "User", "read")).isFalse();
    }

    @Test
    public void hasPermissionEmptyTargetObject() {
        assertThat(target.hasPermission(authentication, null, "read")).isFalse();
        assertThat(target.hasPermission(authentication, "", "read")).isFalse();
    }

    @Test
    public void hasPermissionNotStringPermission() {
        assertThat(target.hasPermission(authentication, new User(), 1L)).isFalse();
        assertThat(target.hasPermission(authentication, 1L, "User", 1L)).isFalse();
    }

    @Test
    public void hasPermission() {
        for (RoleType r: RoleType.values()) {
            authorities.add(new SimpleGrantedAuthority(r.toString()));
        }

        assertThat(target.hasPermission(authentication, new User(), "create")).isTrue();
        assertThat(target.hasPermission(authentication, new User(), "edit")).isTrue();
        assertThat(target.hasPermission(authentication, new User(), "read")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "User", "create")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "User", "edit")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "User", "read")).isTrue();

        assertThat(target.hasPermission(authentication, new Unit(), "create")).isTrue();
        assertThat(target.hasPermission(authentication, new Unit(), "edit")).isTrue();
        assertThat(target.hasPermission(authentication, new Unit(), "read")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "Unit", "create")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "Unit", "edit")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "Unit", "read")).isTrue();

        assertThat(target.hasPermission(authentication, new ESealOrder(), "create")).isTrue();
        assertThat(target.hasPermission(authentication, new ESealOrder(), "edit")).isTrue();
        assertThat(target.hasPermission(authentication, new ESealOrder(), "read")).isTrue();
        assertThat(target.hasPermission(authentication, new ESealOrder(), "sign_and_send")).isTrue();
        assertThat(target.hasPermission(authentication, new ESealOrder(), "activate")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "ESealOrder", "create")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "ESealOrder", "edit")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "ESealOrder", "read")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "ESealOrder", "sign_and_send")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "ESealOrder", "activate")).isTrue();

        assertThat(target.hasPermission(authentication, new CertificateOrder(), "create")).isTrue();
        assertThat(target.hasPermission(authentication, new CertificateOrder(), "edit")).isTrue();
        assertThat(target.hasPermission(authentication, new CertificateOrder(), "read")).isTrue();
        assertThat(target.hasPermission(authentication, new CertificateOrder(), "create_in_batch")).isTrue();
        assertThat(target.hasPermission(authentication, new CertificateOrder(), "sign_and_send")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "CertificateOrder", "create")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "CertificateOrder", "edit")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "CertificateOrder", "read")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "CertificateOrder", "create_in_batch")).isTrue();
        assertThat(target.hasPermission(authentication, 1L, "CertificateOrder", "sign_and_send")).isTrue();
    }


}