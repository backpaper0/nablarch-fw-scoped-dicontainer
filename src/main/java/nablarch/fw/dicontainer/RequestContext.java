package nablarch.fw.dicontainer;

import java.util.Set;

import javax.inject.Provider;

public interface RequestContext {

    <T> T get(final ComponentKey<T> key, final Provider<T> provider, final Set<DestroyMethod> destroyMethods);
}
