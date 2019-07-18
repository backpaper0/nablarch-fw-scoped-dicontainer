package nablarch.fw.dicontainer.scope;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.DestroyMethod;

public interface Scope {

    <T> T getComponent(ComponentId id, Provider<T> provider, DestroyMethod destroyMethod);

    <T> void register(ComponentDefinition<T> definition);

    int dimensions();
}
