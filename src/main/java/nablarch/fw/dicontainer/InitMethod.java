package nablarch.fw.dicontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public interface InitMethod {

    void invoke(final Object component);

    static InitMethod noop() {
        return component -> {
        };
    }

    static InitMethod fromAnnotation(final Class<?> componentType) {
        final MethodCollector methodCollector = new MethodCollector();
        for (Class<?> clazz = componentType; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
            }
        }
        final Set<InitMethod> methods = new LinkedHashSet<>();
        for (final Method method : methodCollector.getMethods()) {
            if (method.isAnnotationPresent(Init.class)) {
                methods.add(new InitMethodImpl(method));
            }
        }
        if (methods.size() > 1) {
            //TODO error
            throw new RuntimeException();
        }
        if (methods.isEmpty()) {
            return noop();
        }
        return methods.iterator().next();
    }

    final class InitMethodImpl implements InitMethod {

        private final Method method;

        public InitMethodImpl(final Method method) {
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
    }
}
