package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Method;

import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.component.impl.reflect.MethodWrapper;

public final class DefaultDestroyMethod implements DestroyMethod {

    private final MethodWrapper method;

    public DefaultDestroyMethod(final Method method) {
        this.method = new MethodWrapper(method);
    }

    @Override
    public void invoke(final Object component) {
        method.invoke(component);
    }
}
