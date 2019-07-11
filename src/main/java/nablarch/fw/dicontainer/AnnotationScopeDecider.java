package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

public final class AnnotationScopeDecider {

    private final Map<Class<?>, Scope> builtinScopes = new HashMap<>();
    private Scope defaultScope;

    public AnnotationScopeDecider() {
        this.defaultScope = new PrototypeScope();

        this.builtinScopes.put(Prototype.class, this.defaultScope);
        this.builtinScopes.put(Singleton.class, new SingletonScope());
    }

    public Scope decide(final Class<?> componentType) {
        final Annotation[] annotations = Arrays.stream(componentType.getAnnotations())
                .filter(a -> a.annotationType().isAnnotationPresent(javax.inject.Scope.class))
                .toArray(Annotation[]::new);
        if (annotations.length == 0) {
            return defaultScope;
        } else if (annotations.length > 1) {
            //TODO error
        }
        final Class<? extends Annotation> annotation = annotations[0].annotationType();
        final Scope scope = builtinScopes.get(annotation);
        if (scope == null) {
            //TODO error
        }
        return scope;
    }

    public void setDefaultScope(final Scope defaultScope) {
        this.defaultScope = defaultScope;
    }
}
