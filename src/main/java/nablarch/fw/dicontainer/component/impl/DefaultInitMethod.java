package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Method;

import nablarch.fw.dicontainer.component.InitMethod;
import nablarch.fw.dicontainer.component.impl.reflect.MethodWrapper;

public final class DefaultInitMethod implements InitMethod {

    private final MethodWrapper method;

    public DefaultInitMethod(final Method method) {
        this.method = new MethodWrapper(method);
    }

    @Override
    public void invoke(final Object component) {
        method.invoke(component);
    }
}
