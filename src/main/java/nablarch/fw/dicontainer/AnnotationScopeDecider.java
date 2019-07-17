package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Singleton;

public final class AnnotationScopeDecider {

    private final Map<Class<?>, Scope> scopes = new HashMap<>();
    private final Scope defaultScope;
    private final PassthroughScope passthroughScope = new PassthroughScope();

    public AnnotationScopeDecider(final Scope defaultScope,
            final Map<Class<?>, Scope> additionalScopes) {
        this.defaultScope = Objects.requireNonNull(defaultScope);
        this.scopes.putAll(Objects.requireNonNull(additionalScopes));

        //builtin scopes
        this.scopes.put(Prototype.class, new PrototypeScope());
        this.scopes.put(Singleton.class, new SingletonScope());
    }

    public AnnotationScopeDecider() {
        this(new PrototypeScope(), Collections.emptyMap());
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
        final Scope scope = scopes.get(annotation);
        if (scope == null) {
            //TODO error
        }
        return scope;
    }

    //FIXME
    void registerScopes(final AnnotationContainerBuilder builder) {
        for (final Scope scope : scopes.values()) {
            registerScope(builder, scope);
        }
        if (scopes.values().stream().anyMatch(a -> a == defaultScope) == false) {
            registerScope(builder, defaultScope);
        }
    }

    private <T extends Scope> void registerScope(final AnnotationContainerBuilder builder,
            final T scope) {
        final Class<T> componentType = (Class<T>) scope.getClass();
        final ComponentKey<T> key = new ComponentKey<>(componentType, Collections.emptySet());
        final InjectableMember injectableConstructor = InjectableMember.passthrough(scope);
        //FIXME 引数errorCollector
        final Set<ObservesMethod> observesMethods = builder.memberFactory.createObservesMethod(
                componentType, new ErrorCollector());
        //FIXME 引数errorCollector
        final DestroyMethod destroyMethod = builder.memberFactory.createDestroyMethod(componentType,
                new ErrorCollector());
        final ComponentDefinition<T> definitionBuilder = ComponentDefinition.<T> builder()
                .injectableConstructor(injectableConstructor)
                .observesMethods(observesMethods)
                .destroyMethod(destroyMethod)
                .scope(passthroughScope)
                .build();
        builder.register(key, definitionBuilder);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Map<Class<?>, Scope> additionalScopes = new HashMap<>();
        private Scope defaultScope = new PrototypeScope();

        private Builder() {
        }

        public Builder addScope(final Class<?> annotationType, final Scope scope) {
            this.additionalScopes.put(annotationType, scope);
            return this;
        }

        public Builder defaultScope(final Scope defaultScope) {
            this.defaultScope = defaultScope;
            return this;
        }

        public AnnotationScopeDecider build() {
            return new AnnotationScopeDecider(defaultScope, additionalScopes);
        }
    }
}
