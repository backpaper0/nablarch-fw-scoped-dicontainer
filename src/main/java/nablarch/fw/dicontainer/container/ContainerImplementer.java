package nablarch.fw.dicontainer.container;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.ComponentId;

public interface ContainerImplementer extends Container {

    <T> T getComponent(ComponentId id);
}
