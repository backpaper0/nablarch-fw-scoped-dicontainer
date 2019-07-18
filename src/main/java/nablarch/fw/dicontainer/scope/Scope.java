package nablarch.fw.dicontainer.scope;

import javax.inject.Provider;

import nablarch.fw.dicontainer.ComponentId;
import nablarch.fw.dicontainer.component.DestroyMethod;

public interface Scope {

    <T> T getComponent(ComponentId id, Provider<T> provider, DestroyMethod destroyMethod);

    int dimensions();
}
