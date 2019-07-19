package nablarch.fw.dicontainer.component.impl;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.container.ContainerImplementer;

public final class ContainerInjectableMember implements InjectableMember {

    @Override
    public Object inject(final ContainerImplementer container, final Object component) {
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
