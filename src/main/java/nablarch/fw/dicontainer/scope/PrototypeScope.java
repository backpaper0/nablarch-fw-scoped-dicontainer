package nablarch.fw.dicontainer.scope;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.ComponentId;

/**
 * プロトタイプスコープ。
 *
 */
public final class PrototypeScope extends AbstractScope {

    @Override
    public <T> T getComponent(final ComponentId id, final Provider<T> provider) {
        return provider.get();
    }

    @Override
    public int dimensions() {
        return Integer.MIN_VALUE;
    }
}
