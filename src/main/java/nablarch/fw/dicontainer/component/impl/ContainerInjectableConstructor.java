package nablarch.fw.dicontainer.component.impl;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.InjectableConstructor;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;

public final class ContainerInjectableConstructor implements InjectableConstructor {

    @Override
    public Object inject(final ContainerImplementer container) {
        return container;
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
    }
}