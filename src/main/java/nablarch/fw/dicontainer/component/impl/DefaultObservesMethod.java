package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Method;

import nablarch.fw.dicontainer.component.ObservesMethod;
import nablarch.fw.dicontainer.component.impl.reflect.MethodWrapper;

public final class DefaultObservesMethod implements ObservesMethod {

    private final MethodWrapper method;
    private final Class<?> eventType;

    public DefaultObservesMethod(final Method method) {
        this.method = new MethodWrapper(method);
        this.eventType = method.getParameterTypes()[0];
    }

    @Override
    public boolean isTarget(final Object event) {
        return eventType.isAssignableFrom(event.getClass());
    }

    @Override
    public void invoke(final Object component, final Object event) {
        method.invoke(component, event);
    }
}
