package nablarch.fw.dicontainer;

import javax.inject.Provider;

public interface RequestContext {

    <T> T getRequestComponent(final ComponentKey<T> key, final Provider<T> provider,
            final DestroyMethod destroyMethod);
}
