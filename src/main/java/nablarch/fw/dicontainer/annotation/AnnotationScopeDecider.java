package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.inject.Singleton;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.Prototype;
import nablarch.fw.dicontainer.config.ComponentDefinition;
import nablarch.fw.dicontainer.config.DestroyMethod;
import nablarch.fw.dicontainer.config.ErrorCollector;
import nablarch.fw.dicontainer.config.InjectableMember;
import nablarch.fw.dicontainer.config.ObservesMethod;
import nablarch.fw.dicontainer.config.PassthroughScope;
import nablarch.fw.dicontainer.config.PrototypeScope;
import nablarch.fw.dicontainer.config.Scope;
import nablarch.fw.dicontainer.config.SingletonScope;
import nablarch.fw.dicontainer.exception.ScopeDuplicatedException;
import nablarch.fw.dicontainer.exception.ScopeNotFoundException;

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

    public Optional<Scope> fromClass(final Class<?> componentType,
            final ErrorCollector errorCollector) {
        final Annotation[] annotations = Arrays.stream(componentType.getAnnotations())
                .filter(a -> a.annotationType().isAnnotationPresent(javax.inject.Scope.class))
                .toArray(Annotation[]::new);
        if (annotations.length == 0) {
            return Optional.of(defaultScope);
        } else if (annotations.length > 1) {
            errorCollector.add(new ScopeDuplicatedException());
            return Optional.empty();
        }
        final Class<? extends Annotation> annotation = annotations[0].annotationType();
        final Scope scope = scopes.get(annotation);
        if (scope == null) {
            errorCollector.add(new ScopeNotFoundException());
            return Optional.empty();
        }
        return Optional.of(scope);
    }

    public Optional<Scope> fromMethod(final Method method, final ErrorCollector errorCollector) {
        final Annotation[] annotations = Arrays.stream(method.getAnnotations())
                .filter(a -> a.annotationType().isAnnotationPresent(javax.inject.Scope.class))
                .toArray(Annotation[]::new);
        if (annotations.length == 0) {
            return Optional.of(defaultScope);
        } else if (annotations.length > 1) {
            errorCollector.add(new ScopeDuplicatedException());
            return Optional.empty();
        }
        final Class<? extends Annotation> annotation = annotations[0].annotationType();
        final Scope scope = scopes.get(annotation);
        if (scope == null) {
            errorCollector.add(new ScopeNotFoundException());
            return Optional.empty();
        }
        return Optional.of(scope);
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

        final ErrorCollector errorCollector = ErrorCollector.wrap(builder);

        final Class<T> componentType = (Class<T>) scope.getClass();
        final ComponentKey<T> key = new ComponentKey<>(componentType);
        final InjectableMember injectableConstructor = InjectableMember.passthrough(scope);
        final Set<ObservesMethod> observesMethods = builder.memberFactory.createObservesMethod(
                componentType, errorCollector);
        final Optional<DestroyMethod> destroyMethod = builder.memberFactory.createDestroyMethod(
                componentType, errorCollector);
        final ComponentDefinition.Builder<T> cdBuilder = ComponentDefinition.builder();
        destroyMethod.ifPresent(cdBuilder::destroyMethod);
        final Optional<ComponentDefinition<T>> definition = cdBuilder
                .injectableConstructor(injectableConstructor)
                .observesMethods(observesMethods)
                .scope(passthroughScope)
                .build();
        definition.ifPresent(a -> builder.register(key, a));
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
