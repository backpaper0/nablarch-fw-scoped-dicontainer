package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Constructor;
import java.util.Objects;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.impl.reflect.ConstructorWrapper;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.container.ContainerImplementer;

public final class InjectableConstructor implements InjectableMember {

    private final ConstructorWrapper constructor;
    private final InjectionComponentResolvers resolvers;

    public InjectableConstructor(final Constructor<?> constructor,
            final InjectionComponentResolvers resolvers) {
        this.constructor = new ConstructorWrapper(constructor);
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    @Override
    public Object inject(final ContainerImplementer container, final Object component) {
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
