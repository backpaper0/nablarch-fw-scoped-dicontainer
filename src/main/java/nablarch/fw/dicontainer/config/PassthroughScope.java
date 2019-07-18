package nablarch.fw.dicontainer.config;

import javax.inject.Provider;

import nablarch.fw.dicontainer.ComponentId;

public final class PassthroughScope implements Scope {

    @Override
    public <T> T getComponent(final ComponentId id, final Provider<T> provider,
            final DestroyMethod destroyMethod) {
        return provider.get();
    }

    @Override
    public int dimensions() {
        return Integer.MAX_VALUE;
    }
}
