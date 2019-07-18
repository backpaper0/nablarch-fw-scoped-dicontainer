package nablarch.fw.dicontainer.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;

public final class InjectableConstructor implements InjectableMember {

    private final Constructor<?> constructor;
    private final List<InjectionComponentResolver> resolvers;

    public InjectableConstructor(final Constructor<?> constructor,
            final List<InjectionComponentResolver> resolvers) {
        this.constructor = Objects.requireNonNull(constructor);
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    @Override
    public Object inject(final Container container, final Object component) {
        final Object[] args = resolvers.stream().map(resolver -> resolver.resolve(container))
                .toArray();
        try {
            if (constructor.isAccessible() == false) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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
