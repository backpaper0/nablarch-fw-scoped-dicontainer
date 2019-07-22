package nablarch.fw.dicontainer.nablarch;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.handler.HandlerFactory;

public final class ContainerLookupHandlerFactory implements HandlerFactory {

    @Override
    public Object create(final Class<?> clazz)
            throws InstantiationException, IllegalAccessException {
        final Container container = ContainerImplementers.get();
        return container.getComponent(clazz);
    }
}
