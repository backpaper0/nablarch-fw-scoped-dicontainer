package nablarch.fw.dicontainer.component.impl;

import java.util.Objects;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.InjectableConstructor;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;

public final class PassthroughInjectableConstructor implements InjectableConstructor {

    private final Object instance;

    public PassthroughInjectableConstructor(final Object instance) {
        this.instance = Objects.requireNonNull(instance);
    }

    @Override
    public Object inject(final ContainerImplementer container) {
        return instance;
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
    }
}
