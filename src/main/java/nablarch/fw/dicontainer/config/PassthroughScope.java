package nablarch.fw.dicontainer.config;

import javax.inject.Provider;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.Scope;

public final class PassthroughScope implements Scope {

    @Override
    public <T> T getComponent(final ComponentKey<T> key, final Provider<T> provider,
            final DestroyMethod destroyMethod) {
        return provider.get();
    }

    @Override
    public int dimensions() {
        return Integer.MAX_VALUE;
    }
}
