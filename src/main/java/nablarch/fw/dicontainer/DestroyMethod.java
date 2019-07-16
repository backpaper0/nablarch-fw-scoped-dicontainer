package nablarch.fw.dicontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import nablarch.fw.dicontainer.servlet.SerializedDestroyMethod;

public interface DestroyMethod {

    void invoke(final Object component);

    SerializedDestroyMethod serialize();

    static DestroyMethod noop() {
        return new DestroyMethod() {

            @Override
            public SerializedDestroyMethod serialize() {
                return SerializedDestroyMethod.noop();
            }

            @Override
            public void invoke(final Object component) {
            }
        };
    }

    static DestroyMethod fromAnnotation(final Class<?> componentType) {
        final MethodCollector methodCollector = new MethodCollector();
        for (Class<?> clazz = componentType; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
            }
        }
        final Set<DestroyMethod> methods = new LinkedHashSet<>();
        for (final Method method : methodCollector.getMethods()) {
            if (method.isAnnotationPresent(Destroy.class)) {
                methods.add(new DestroyMethodImpl(method));
            }
        }
        if (methods.isEmpty()) {
            return noop();
        }
        return methods.iterator().next();
    }

    static DestroyMethod fromMethod(final Method method) {
        return new DestroyMethodImpl(method);
    }

    final class DestroyMethodImpl implements DestroyMethod {
        private final Method method;

        public DestroyMethodImpl(final Method method) {
            this.method = Objects.requireNonNull(method);
        }

        @Override
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

        @Override
        public SerializedDestroyMethod serialize() {
            return SerializedDestroyMethod.fromMethod(method);
        }
    }
}
