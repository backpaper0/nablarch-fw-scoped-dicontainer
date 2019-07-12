package nablarch.fw.dicontainer;

import java.util.Set;

import javax.inject.Provider;

public interface SessionContext {

    <T> T get(final ComponentKey<T> key, final Provider<T> provider, final Set<DestroyMethod> destroyMethods);
}
