package nablarch.fw.dicontainer.web.context;

import javax.inject.Provider;

import nablarch.fw.dicontainer.ComponentId;
import nablarch.fw.dicontainer.component.DestroyMethod;

public interface SessionContext {

    <T> T getSessionComponent(final ComponentId id, final Provider<T> provider,
            final DestroyMethod destroyMethod);
}
