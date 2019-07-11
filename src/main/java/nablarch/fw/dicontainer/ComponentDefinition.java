package nablarch.fw.dicontainer;

import java.util.Objects;
import java.util.Set;

import javax.inject.Provider;

public final class ComponentDefinition<T> {

    private final InjectableConstructor<T> injectableConstructor;
    private final Set<InjectableMember> injectableMembers;
    private final Scope scope;

    public ComponentDefinition(final InjectableConstructor<T> injectableConstructor, final Set<InjectableMember> injectableMembers,
            final Scope scope) {
        this.injectableConstructor = Objects.requireNonNull(injectableConstructor);
        this.injectableMembers = Objects.requireNonNull(injectableMembers);
        this.scope = Objects.requireNonNull(scope);
    }

    public static <T> AnnotationBaseBuilder<T> builderFromAnnotation(final Class<T> componentType) {
        return new AnnotationBaseBuilder<>(componentType);
    }

    public T getComponent(final Container container, final ComponentKey<T> key) {
        final Provider<T> provider = new Provider<T>() {
            @Override
            public T get() {
                final T component = injectableConstructor.inject(container, null);
                injectableMembers.forEach(a -> a.inject(container, component));
                return component;
            }
        };
        return scope.getComponent(key, provider);
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
            final InjectableConstructor<T> injectableConstructor = InjectableConstructor.fromAnnotation(componentType);
            final Set<InjectableMember> injectableMembers = InjectableMember.fromAnnotation(componentType);
            return new ComponentDefinition<>(injectableConstructor, injectableMembers, scope);
        }
    }
}
