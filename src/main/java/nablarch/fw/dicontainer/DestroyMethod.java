package nablarch.fw.dicontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

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
