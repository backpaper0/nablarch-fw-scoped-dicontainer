package nablarch.fw.dicontainer;

import javax.inject.Provider;

public interface SessionContext {

    <T> T getSessionComponent(final ComponentKey<T> key, final Provider<T> provider,
            final DestroyMethod destroyMethod);
}
