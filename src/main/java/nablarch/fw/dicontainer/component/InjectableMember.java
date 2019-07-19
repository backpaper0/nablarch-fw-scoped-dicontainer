package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.container.ContainerImplementer;

public interface InjectableMember {

    Object inject(ContainerImplementer container, Object component);

    void validate(ContainerBuilder<?> containerBuilder, ComponentDefinition<?> self);

    void validateCycleDependency(CycleDependencyValidationContext context);
}