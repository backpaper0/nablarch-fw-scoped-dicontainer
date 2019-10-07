package nablarch.fw.dicontainer.nablarch;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.handler.DelegateFactory;

public final class ContainerLookupHandlerFactory implements DelegateFactory {

    @Override
    public Object create(final Class<?> clazz) {
        final Container container = ContainerImplementers.get();
        return container.getComponent(clazz);
    }
}
