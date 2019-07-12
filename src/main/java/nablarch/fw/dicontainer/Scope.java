package nablarch.fw.dicontainer;

import java.util.Set;

import javax.inject.Provider;

public interface Scope {

    <T> T getComponent(ComponentKey<T> key, Provider<T> provider,
            Set<DestroyMethod> destroyMethods);
}
