package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.container.ContainerBuilder;

public interface ObservesMethod {

    boolean isTarget(final Object event);

    void invoke(final Object component, final Object event);

    void validate(ContainerBuilder<?> containerBuilder, ComponentDefinition<?> self);
}
