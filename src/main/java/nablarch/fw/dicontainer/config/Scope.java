package nablarch.fw.dicontainer.config;

import javax.inject.Provider;

import nablarch.fw.dicontainer.ComponentId;

public interface Scope {

    <T> T getComponent(ComponentId id, Provider<T> provider, DestroyMethod destroyMethod);

    int dimensions();
}
