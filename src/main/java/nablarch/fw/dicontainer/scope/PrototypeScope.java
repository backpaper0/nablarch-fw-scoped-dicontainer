package nablarch.fw.dicontainer.scope;

import javax.inject.Provider;

import nablarch.fw.dicontainer.ComponentId;
import nablarch.fw.dicontainer.component.DestroyMethod;

public final class PrototypeScope implements Scope {

    @Override
    public <T> T getComponent(final ComponentId id, final Provider<T> provider,
            final DestroyMethod destroyMethod) {
        return provider.get();
    }

    @Override
    public int dimensions() {
        return Integer.MIN_VALUE;
    }
}
