package nablarch.fw.dicontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import nablarch.fw.dicontainer.servlet.SerializedDestroyMethod;

public final class DestroyMethod {

    private final Method method;

    public DestroyMethod(final Method method) {
        this.method = Objects.requireNonNull(method);
    }

    public static Set<DestroyMethod> fromAnnotation(final Class<?> componentType) {
        final MethodCollector methodCollector = new MethodCollector();
        for (Class<?> clazz = componentType; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
            }
        }
        final Set<DestroyMethod> methods = new LinkedHashSet<>();
        for (final Method method : methodCollector.getMethods()) {
            if (method.isAnnotationPresent(Destroy.class)) {
                methods.add(new DestroyMethod(method));
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

    public SerializedDestroyMethod serialize() {
        return new SerializedDestroyMethod(method.getDeclaringClass(), method.getName());
    }
}
