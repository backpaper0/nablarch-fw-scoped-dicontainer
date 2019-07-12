package nablarch.fw.dicontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class InitMethod {

    private final Method method;

    public InitMethod(final Method method) {
        this.method = Objects.requireNonNull(method);
    }

    public static Set<InitMethod> fromAnnotation(final Class<?> componentType) {
        final MethodCollector methodCollector = new MethodCollector();
        for (Class<?> clazz = componentType; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
            }
        }
        final Set<InitMethod> methods = new LinkedHashSet<>();
        for (final Method method : methodCollector.getMethods()) {
            if (method.isAnnotationPresent(Init.class)) {
                methods.add(new InitMethod(method));
            }
        }
        return methods;
    }

    public void invoke(final Object component) {
        if (method.isAccessible() == false) {
            method.setAccessible(true);
        }
        try {
            method.invoke(component);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException();
        }
    }
}
