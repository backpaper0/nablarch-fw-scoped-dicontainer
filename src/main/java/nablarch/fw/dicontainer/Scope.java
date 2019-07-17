package nablarch.fw.dicontainer;

import javax.inject.Provider;

import nablarch.fw.dicontainer.config.DestroyMethod;

public interface Scope {

    <T> T getComponent(ComponentId id, Provider<T> provider, DestroyMethod destroyMethod);

    int dimensions();
}
