package nablarch.fw.dicontainer.component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.impl.NoopDestroyMethod;
import nablarch.fw.dicontainer.component.impl.NoopInitMethod;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.scope.Scope;

public final class ComponentDefinition<T> {

    private final ComponentId id;
    private final InjectableMember injectableConstructor;
    private final List<InjectableMember> injectableMembers;
    private final List<ObservesMethod> observesMethods;
    private final InitMethod initMethod;
    private final DestroyMethod destroyMethod;
    private final List<FactoryMethod> factoryMethods;
    private final Scope scope;

    private ComponentDefinition(final ComponentId id,
            final InjectableMember injectableConstructor,
            final List<InjectableMember> injectableMembers,
            final List<ObservesMethod> observesMethods,
            final InitMethod initMethod,
            final DestroyMethod destroyMethod,
            final List<FactoryMethod> factoryMethods,
            final Scope scope) {
        this.id = Objects.requireNonNull(id);
        this.injectableConstructor = Objects.requireNonNull(injectableConstructor);
        this.injectableMembers = Objects.requireNonNull(injectableMembers);
        this.observesMethods = Objects.requireNonNull(observesMethods);
        this.initMethod = Objects.requireNonNull(initMethod);
        this.destroyMethod = Objects.requireNonNull(destroyMethod);
        this.factoryMethods = Objects.requireNonNull(factoryMethods);
        this.scope = Objects.requireNonNull(scope);

        this.scope.register(this);
    }

    public ComponentId getId() {
        return id;
    }

    public void validate(final ContainerBuilder<?> containerBuilder) {
        injectableConstructor.validate(containerBuilder, this);
        for (final InjectableMember injectableMember : injectableMembers) {
            injectableMember.validate(containerBuilder, this);
        }
    }

    public boolean isNarrowScope(final ComponentDefinition<?> injected) {
        return scope.dimensions() <= injected.scope.dimensions();
    }

    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        injectableConstructor.validateCycleDependency(context.createSubContext());
        for (final InjectableMember injectableMember : injectableMembers) {
            injectableMember.validateCycleDependency(context.createSubContext());
        }
    }

    public void applyFactories(final ContainerBuilder<?> containerBuilder) {
        for (final FactoryMethod factoryMethod : factoryMethods) {
            factoryMethod.apply(containerBuilder);
        }
    }

    public T getComponent(final ContainerImplementer container) {
        Objects.requireNonNull(container);
        final Provider<T> provider = new Provider<T>() {
            @Override
            public T get() {
                final Object component = injectableConstructor.inject(container, null);
                for (final InjectableMember injectableMember : injectableMembers) {
                    injectableMember.inject(container, component);
                }
                initMethod.invoke(component);
                return (T) component;
            }
        };
        return scope.getComponent(id, provider);
    }

    public void fire(final ContainerImplementer container, final Object event) {
        for (final ObservesMethod observesMethod : observesMethods) {
            if (observesMethod.isTarget(event)) {
                final T component = getComponent(container);
                observesMethod.invoke(component, event);
            }
        }
    }

    public void destroyComponent(final T component) {
        destroyMethod.invoke(component);
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {

        private final ComponentId id = ComponentId.generate();
        private InjectableMember injectableConstructor;
        private List<InjectableMember> injectableMembers = Collections.emptyList();
        private List<ObservesMethod> observesMethods = Collections.emptyList();
        private InitMethod initMethod = new NoopInitMethod();
        private DestroyMethod destroyMethod = new NoopDestroyMethod();
        private List<FactoryMethod> factoryMethods = Collections.emptyList();
        private Scope scope;

        private Builder() {
        }

        public ComponentId id() {
            return id;
        }

        public Builder<T> injectableConstructor(
                final InjectableMember injectableConstructor) {
            this.injectableConstructor = injectableConstructor;
            return this;
        }

        public Builder<T> injectableMembers(final List<InjectableMember> injectableMembers) {
            this.injectableMembers = injectableMembers;
            return this;
        }

        public Builder<T> observesMethods(final List<ObservesMethod> observesMethods) {
            this.observesMethods = observesMethods;
            return this;
        }

        public Builder<T> initMethod(final InitMethod initMethod) {
            this.initMethod = initMethod;
            return this;
        }

        public Builder<T> destroyMethod(final DestroyMethod destroyMethod) {
            this.destroyMethod = destroyMethod;
            return this;
        }

        public Builder<T> factoryMethods(final List<FactoryMethod> factoryMethods) {
            this.factoryMethods = factoryMethods;
            return this;
        }

        public Builder<T> scope(final Scope scope) {
            this.scope = scope;
            return this;
        }

        public Optional<ComponentDefinition<T>> build() {
            if (injectableConstructor == null) {
                return Optional.empty();
            }
            if (scope == null) {
                return Optional.empty();
            }
            final ComponentDefinition<T> cd = new ComponentDefinition<>(id, injectableConstructor,
                    injectableMembers, observesMethods, initMethod, destroyMethod, factoryMethods,
                    scope);
            return Optional.of(cd);
        }
    }
}
