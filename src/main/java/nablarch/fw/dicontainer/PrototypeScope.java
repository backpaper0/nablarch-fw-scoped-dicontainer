package nablarch.fw.dicontainer;

import javax.inject.Provider;

public final class PrototypeScope implements Scope {

    @Override
    public <T> T getComponent(final ComponentKey<T> key, final Provider<T> provider) {
        return provider.get();
    }
}
