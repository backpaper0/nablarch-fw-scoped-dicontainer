package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

public final class AnnotationScopeDecider {

    private final Map<Class<?>, Scope> builtinScopes = new HashMap<>();
    private Scope defaultScope;
    private final PassthroughScope passthroughScope = new PassthroughScope();

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

    //FIXME
    void registerScopes(final AnnotationContainerBuilder builder) {
        for (final Scope scope : builtinScopes.values()) {
            registerScope(builder, scope);
        }
        if (builtinScopes.values().stream().anyMatch(a -> a == defaultScope) == false) {
            registerScope(builder, defaultScope);
        }
    }

    private <T extends Scope> void registerScope(final AnnotationContainerBuilder builder,
            final T scope) {
        final Class<T> componentType = (Class<T>) scope.getClass();
        final ComponentKey<T> key = new ComponentKey<>(componentType, Collections.emptySet());
        final InjectableMember injectableConstructor = (container, component) -> scope;
        final Set<InjectableMember> injectableMembers = Collections.emptySet();
        final Set<ObservesMethod> observesMethods = ObservesMethod.fromAnnotation(componentType);
        final Set<InitMethod> initMethods = Collections.emptySet();
        final Set<DestroyMethod> destroyMethods = DestroyMethod.fromAnnotation(componentType);
        final ComponentDefinition<T> definition = new ComponentDefinition<>(injectableConstructor,
                injectableMembers, observesMethods, initMethods, destroyMethods, passthroughScope);
        builder.register(key, definition);
    }
}
