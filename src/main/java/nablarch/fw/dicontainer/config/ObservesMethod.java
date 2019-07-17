package nablarch.fw.dicontainer.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public final class ObservesMethod {

    private final Method method;
    private final Class<?> eventType;

    public ObservesMethod(final Method method) {
        this.method = Objects.requireNonNull(method);
        this.eventType = method.getParameterTypes()[0];
    }

    public boolean isTarget(final Object event) {
        return eventType.isAssignableFrom(event.getClass());
    }

    public void invoke(final Object component, final Object event) {
        if (method.isAccessible() == false) {
            method.setAccessible(true);
        }
        try {
            method.invoke(component, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
