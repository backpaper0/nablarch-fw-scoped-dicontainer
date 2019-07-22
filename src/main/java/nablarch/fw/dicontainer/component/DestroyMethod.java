package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.container.ContainerBuilder;

public interface DestroyMethod {

    void invoke(final Object component);

    void validate(ContainerBuilder<?> containerBuilder, ComponentDefinition<?> self);
}
