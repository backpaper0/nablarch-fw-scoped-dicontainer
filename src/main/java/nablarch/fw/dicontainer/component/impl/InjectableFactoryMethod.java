package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Method;
import java.util.Objects;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.impl.reflect.MethodWrapper;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.exception.FactoryMethodSignatureException;

public final class InjectableFactoryMethod implements InjectableMember {

    private final ComponentId factoryId;
    private final MethodWrapper method;
    private final InjectionComponentResolvers resolvers;

    public InjectableFactoryMethod(final ComponentId factoryId, final Method method,
            final InjectionComponentResolvers resolvers) {
        this.factoryId = Objects.requireNonNull(factoryId);
        this.method = new MethodWrapper(method);
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    @Override
    public Object inject(final ContainerImplementer container, final Object component) {
        final Object factoryComponent = container.getComponent(factoryId);
        final Object[] args = resolvers.resolve(container);
        return method.invoke(factoryComponent, args);
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        if (method.isStatic()) {
            containerBuilder.addError(new FactoryMethodSignatureException(
                    "Factory method [" + method + "] must not be static."));
            return;
        }

        if (method.getReturnType() == Void.TYPE) {
            containerBuilder.addError(new FactoryMethodSignatureException(
                    "Factory method [" + method + "] must be no return."));
            return;
        }

        if (method.getParameterCount() != 0) {
            containerBuilder.addError(new FactoryMethodSignatureException(
                    "Factory method [" + method + "] must have one parameter."));
            return;
        }

        resolvers.validate(containerBuilder, self);
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        resolvers.validateCycleDependency(context);
    }
}
