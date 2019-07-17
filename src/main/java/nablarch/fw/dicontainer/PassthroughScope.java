package nablarch.fw.dicontainer;

import javax.inject.Provider;

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
