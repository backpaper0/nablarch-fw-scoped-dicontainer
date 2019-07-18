package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Method;
import java.util.Objects;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.impl.reflect.MethodWrapper;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.container.ContainerImplementer;

public final class InjectableFactoryMethod implements InjectableMember {

    private final ComponentId componentId;
    private final MethodWrapper method;
    private final InjectionComponentResolvers resolvers;

    public InjectableFactoryMethod(final ComponentId componentId, final Method method,
            final InjectionComponentResolvers resolvers) {
        this.componentId = Objects.requireNonNull(componentId);
        this.method = new MethodWrapper(method);
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    @Override
    public Object inject(final ContainerImplementer container, final Object component) {
        final Object factoryComponent = container.getComponent(componentId);
        final Object[] args = resolvers.resolve(container);
        return method.invoke(factoryComponent, args);
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
