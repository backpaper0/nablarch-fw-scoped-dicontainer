package nablarch.fw.dicontainer;

import java.util.Objects;
import java.util.Set;

import javax.inject.Provider;

public final class ComponentDefinition<T> {

    private final InjectableMember injectableConstructor;
    private final Set<InjectableMember> injectableMembers;
    private final Set<ObservesMethod> observesMethods;
    private final Set<InitMethod> initMethods;
    private final Set<DestroyMethod> destroyMethods;
    private final Scope scope;

    public ComponentDefinition(final InjectableMember injectableConstructor,
            final Set<InjectableMember> injectableMembers,
            final Set<ObservesMethod> observesMethods,
            final Set<InitMethod> initMethods,
            final Set<DestroyMethod> destroyMethods,
            final Scope scope) {
        this.injectableConstructor = Objects.requireNonNull(injectableConstructor);
        this.injectableMembers = Objects.requireNonNull(injectableMembers);
        this.observesMethods = Objects.requireNonNull(observesMethods);
        this.initMethods = Objects.requireNonNull(initMethods);
        this.destroyMethods = Objects.requireNonNull(destroyMethods);
        this.scope = Objects.requireNonNull(scope);
    }

    public static <T> AnnotationBaseBuilder<T> builderFromAnnotation(final Class<T> componentType) {
        return new AnnotationBaseBuilder<>(componentType);
    }

    public T getComponent(final Container container, final ComponentKey<T> key) {
        final Provider<T> provider = new Provider<T>() {
            @Override
            public T get() {
                final Object component = injectableConstructor.inject(container, null);
                for (final InjectableMember injectableMember : injectableMembers) {
                    injectableMember.inject(container, component);
                }
                for (final InitMethod initMethod : initMethods) {
                    initMethod.invoke(component);
                }
                return (T) component;
            }
        };
        return scope.getComponent(key, provider, destroyMethods);
    }

    public void fire(final Container container, final ComponentKey<T> key, final Object event) {
        for (final ObservesMethod observesMethod : observesMethods) {
            if (observesMethod.isTarget(event)) {
                final T component = getComponent(container, key);
                observesMethod.invoke(component, event);
            }
        }
    }

    public static final class AnnotationBaseBuilder<T> {

        private final Class<T> componentType;
        private Scope scope;

        public AnnotationBaseBuilder(final Class<T> componentType) {
            this.componentType = Objects.requireNonNull(componentType);
        }

        public AnnotationBaseBuilder<T> scope(final Scope scope) {
            this.scope = scope;
            return this;
        }

        public ComponentDefinition<T> build() {
            final InjectableConstructor<T> injectableConstructor = InjectableConstructor
                    .fromAnnotation(componentType);
            final Set<InjectableMember> injectableMembers = InjectableMember
                    .fromAnnotation(componentType);
            final Set<ObservesMethod> observesMethods = ObservesMethod
                    .fromAnnotation(componentType);
            final Set<InitMethod> initMethods = InitMethod.fromAnnotation(componentType);
            final Set<DestroyMethod> destroyMethods = DestroyMethod.fromAnnotation(componentType);
            return new ComponentDefinition<>(injectableConstructor, injectableMembers,
                    observesMethods, initMethods, destroyMethods, scope);
        }
    }
}
