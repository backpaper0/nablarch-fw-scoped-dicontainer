package nablarch.fw.dicontainer.web;

import javax.inject.Provider;

import nablarch.fw.dicontainer.ComponentId;
import nablarch.fw.dicontainer.config.DestroyMethod;

public interface RequestContext {

    <T> T getRequestComponent(final ComponentId id, final Provider<T> provider,
            final DestroyMethod destroyMethod);
}
