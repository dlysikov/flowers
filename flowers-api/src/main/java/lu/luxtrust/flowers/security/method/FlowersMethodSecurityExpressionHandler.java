package lu.luxtrust.flowers.security.method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;

public class FlowersMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final MethodSecurityContext methodSecurityContext;
    private final AuthenticationTrustResolver trustResolver;

    public FlowersMethodSecurityExpressionHandler(MethodSecurityContext methodSecurityContext,
                                                  AuthenticationTrustResolver trustResolver) {
        this.methodSecurityContext = methodSecurityContext;
        this.trustResolver = trustResolver;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        FlowersMethodSecurityExpressionRoot root = new FlowersMethodSecurityExpressionRoot(authentication, methodSecurityContext);
        root.setPermissionEvaluator(new FlowersPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }
}
