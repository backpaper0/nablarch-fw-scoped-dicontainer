package nablarch.fw.dicontainer.web;

import javax.inject.Provider;

import nablarch.fw.dicontainer.ComponentId;
import nablarch.fw.dicontainer.config.DestroyMethod;

public interface SessionContext {

    <T> T getSessionComponent(final ComponentId id, final Provider<T> provider,
            final DestroyMethod destroyMethod);
}
