package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.container.ContainerBuilder;

public interface InitMethod {

    void invoke(final Object component);

    void validate(ContainerBuilder<?> containerBuilder, ComponentDefinition<?> self);
}
