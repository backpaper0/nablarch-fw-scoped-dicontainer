package nablarch.fw.dicontainer.component.impl;

import nablarch.fw.dicontainer.component.InitMethod;

public final class NoopInitMethod implements InitMethod {

    @Override
    public void invoke(final Object component) {
    }
}
