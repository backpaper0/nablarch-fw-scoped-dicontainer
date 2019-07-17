package nablarch.fw.dicontainer;

import javax.inject.Provider;

import nablarch.fw.dicontainer.config.DestroyMethod;

public interface Scope {

    <T> T getComponent(ComponentKey<T> key, Provider<T> provider, DestroyMethod destroyMethod);

    int dimensions();
}
