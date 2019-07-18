package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.inject.Singleton;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.Prototype;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.ObservesMethod;
import nablarch.fw.dicontainer.component.impl.PassthroughInjectableMember;
import nablarch.fw.dicontainer.exception.ErrorCollector;
import nablarch.fw.dicontainer.exception.ScopeDuplicatedException;
import nablarch.fw.dicontainer.exception.ScopeNotFoundException;
import nablarch.fw.dicontainer.scope.PassthroughScope;
import nablarch.fw.dicontainer.scope.PrototypeScope;
import nablarch.fw.dicontainer.scope.Scope;
import nablarch.fw.dicontainer.scope.SingletonScope;

public final class AnnotationScopeDecider {

    private final AnnotationSet scopeAnnotations;
    private final Scope defaultScope;
    private final Map<Class<?>, Scope> scopes;
    private final PassthroughScope passthroughScope = new PassthroughScope();

    private AnnotationScopeDecider(final AnnotationSet scopeAnnotations, final Scope defaultScope,
            final Map<Class<?>, Scope> scopes) {
        this.scopeAnnotations = Objects.requireNonNull(scopeAnnotations);
        this.defaultScope = Objects.requireNonNull(defaultScope);
        this.scopes = Objects.requireNonNull(scopes);
    }

    public Optional<Scope> fromClass(final Class<?> componentType,
            final ErrorCollector errorCollector) {
        final Annotation[] annotations = Arrays.stream(componentType.getAnnotations())
                .filter(a -> scopeAnnotations.isAnnotationPresent(a.annotationType()))
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
                .filter(a -> scopeAnnotations.isAnnotationPresent(a.annotationType()))
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
        final InjectableMember injectableConstructor = new PassthroughInjectableMember(scope);
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

    public static AnnotationScopeDecider createDefault() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private AnnotationSet scopeAnnotations = new AnnotationSet(javax.inject.Scope.class);
        private Scope defaultScope;
        private final Map<Class<?>, Scope> scopes = new HashMap<>();

        private Builder() {
            final Scope prototypeScope = new PrototypeScope();
            this.scopes.put(Prototype.class, prototypeScope);
            this.scopes.put(Singleton.class, new SingletonScope());
            this.defaultScope = prototypeScope;
        }

        public Builder scopeAnnotations(final Class<? extends Annotation> annotations) {
            this.scopeAnnotations = new AnnotationSet(annotations);
            return this;
        }

        public Builder defaultScope(final Scope defaultScope) {
            this.defaultScope = defaultScope;
            return this;
        }

        public Builder addScope(final Class<?> annotationType, final Scope scope) {
            this.scopes.put(annotationType, scope);
            return this;
        }

        public AnnotationScopeDecider build() {
            return new AnnotationScopeDecider(scopeAnnotations, defaultScope, scopes);
        }
    }
}
