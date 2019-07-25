package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Constructor;
import java.util.Objects;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.InjectableConstructor;
import nablarch.fw.dicontainer.component.impl.reflect.ConstructorWrapper;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;

public final class DefaultInjectableConstructor implements InjectableConstructor {

    private final ConstructorWrapper constructor;
    private final InjectionComponentResolvers resolvers;

    public DefaultInjectableConstructor(final Constructor<?> constructor,
            final InjectionComponentResolvers resolvers) {
        this.constructor = new ConstructorWrapper(constructor);
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    @Override
    public Object inject(final ContainerImplementer container) {
        final Object[] args = resolvers.resolve(container);
        return constructor.newInstance(args);
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        resolvers.validate(containerBuilder, self);
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        resolvers.validateCycleDependency(context);
    }
}
