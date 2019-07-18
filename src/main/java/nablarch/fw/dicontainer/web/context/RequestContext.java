package nablarch.fw.dicontainer.web.context;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.DestroyMethod;

public interface RequestContext {

    <T> T getRequestComponent(final ComponentId id, final Provider<T> provider,
            final DestroyMethod destroyMethod);
}
