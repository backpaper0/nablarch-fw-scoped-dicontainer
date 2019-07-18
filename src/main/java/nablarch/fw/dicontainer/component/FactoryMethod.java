package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.container.ContainerBuilder;

public interface FactoryMethod {

    void apply(ContainerBuilder<?> containerBuilder);
}
