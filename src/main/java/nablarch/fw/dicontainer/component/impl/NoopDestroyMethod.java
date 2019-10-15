package nablarch.fw.dicontainer.component.impl;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.container.ContainerBuilder;

/**
 * 何もしない{@link DestroyMethod}実装クラス。
 */
public final class NoopDestroyMethod implements DestroyMethod {

    @Override
    public void invoke(final Object component) {
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
    }
}
