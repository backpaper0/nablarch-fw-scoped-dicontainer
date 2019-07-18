package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.component.impl.reflect.MethodWrapper;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.container.ContainerImplementer;

public class InjectableFactoryMethod implements InjectableMember {

    private final ComponentId componentId;
    private final MethodWrapper method;
    private final List<InjectionComponentResolver> resolvers;

    public InjectableFactoryMethod(final ComponentId componentId, final Method method,
            final List<InjectionComponentResolver> resolvers) {
        this.componentId = Objects.requireNonNull(componentId);
        this.method = new MethodWrapper(method);
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    @Override
    public Object inject(final ContainerImplementer container, final Object component) {
        final Object factoryComponent = container.getComponent(componentId);
        final Object[] args = resolvers.stream().map(resolver -> resolver.resolve(container))
                .toArray();
        return method.invoke(factoryComponent, args);
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
