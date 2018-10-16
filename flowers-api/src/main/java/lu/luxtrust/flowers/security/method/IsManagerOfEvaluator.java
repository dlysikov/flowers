package lu.luxtrust.flowers.security.method;

import org.springframework.security.core.Authentication;

public interface IsManagerOfEvaluator<T, A extends Authentication> {
    boolean isManagerOf(A authentication, Long id);
    boolean isManagerOf(A authentication, T targetObject);
    Class<T> supportedType();
}
