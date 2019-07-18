package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.component.impl.reflect.ConstructorWrapper;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.container.ContainerImplementer;

public final class InjectableConstructor implements InjectableMember {

    private final ConstructorWrapper constructor;
    private final List<InjectionComponentResolver> resolvers;

    public InjectableConstructor(final Constructor<?> constructor,
            final List<InjectionComponentResolver> resolvers) {
        this.constructor = new ConstructorWrapper(constructor);
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    @Override
    public Object inject(final ContainerImplementer container, final Object component) {
        final Object[] args = resolvers.stream().map(resolver -> resolver.resolve(container))
                .toArray();
        return constructor.newInstance(args);
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        for (final InjectionComponentResolver resolver : resolvers) {
            resolver.validate(containerBuilder, self);
        }
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        for (final InjectionComponentResolver resolver : resolvers) {
            resolver.validateCycleDependency(context.createSubContext());
        }
    }
}
