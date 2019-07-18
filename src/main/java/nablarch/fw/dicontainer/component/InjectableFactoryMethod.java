package nablarch.fw.dicontainer.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.container.ContainerImplementer;

public class InjectableFactoryMethod implements InjectableMember {

    private final ComponentId componentId;
    private final Method method;
    private final List<InjectionComponentResolver> resolvers;

    public InjectableFactoryMethod(final ComponentId componentId, final Method method,
            final List<InjectionComponentResolver> resolvers) {
        this.componentId = Objects.requireNonNull(componentId);
        this.method = Objects.requireNonNull(method);
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    @Override
    public Object inject(final ContainerImplementer container, final Object component) {
        final Object factoryComponent = container.getComponent(componentId);
        final Object[] args = resolvers.stream().map(resolver -> resolver.resolve(container))
                .toArray();
        if (method.isAccessible() == false) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(factoryComponent, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
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
            resolver.validateCycleDependency(context);
        }
    }
}
