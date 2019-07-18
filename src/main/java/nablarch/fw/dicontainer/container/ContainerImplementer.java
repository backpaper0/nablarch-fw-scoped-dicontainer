package nablarch.fw.dicontainer.container;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;

public interface ContainerImplementer extends Container {

    <T> T getComponent(ComponentId id);

    <T> ComponentDefinition<T> getComponentDefinition(ComponentId id);
}
