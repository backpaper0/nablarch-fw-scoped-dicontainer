package nablarch.fw.dicontainer.web;

import javax.inject.Provider;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.config.DestroyMethod;

public interface SessionContext {

    <T> T getSessionComponent(final ComponentKey<T> key, final Provider<T> provider,
            final DestroyMethod destroyMethod);
}
