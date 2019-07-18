package nablarch.fw.dicontainer.component.impl;

import nablarch.fw.dicontainer.component.InitMethod;

public class NoopInitMethod implements InitMethod {

    @Override
    public void invoke(final Object component) {
    }
}
