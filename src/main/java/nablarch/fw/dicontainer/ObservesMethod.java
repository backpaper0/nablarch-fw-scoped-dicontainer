package nablarch.fw.dicontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class ObservesMethod {

    private final Method method;
    private final Class<?> eventType;

    public ObservesMethod(final Method method) {
        this.method = Objects.requireNonNull(method);
        this.eventType = method.getParameterTypes()[0];
    }

    public static Set<ObservesMethod> fromAnnotation(final Class<?> componentType) {

        final MethodCollector methodCollector = new MethodCollector();
        for (Class<?> clazz = componentType; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
            }
        }

        final Set<ObservesMethod> observesMethods = new LinkedHashSet<>();
        for (final Method method : methodCollector.getMethods()) {
            if (method.isAnnotationPresent(Observes.class)) {
                if (method.getParameterCount() != 1) {
                    //TODO error
                }
                final ObservesMethod observesMethod = new ObservesMethod(method);
                observesMethods.add(observesMethod);
            }
        }

        return observesMethods;
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
